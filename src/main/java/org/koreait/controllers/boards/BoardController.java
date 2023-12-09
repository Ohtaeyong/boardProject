package org.koreait.controllers.boards;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.commons.ListData;
import org.koreait.commons.MemberUtil;
import org.koreait.commons.ScriptExceptionProcess;
import org.koreait.commons.Utils;
import org.koreait.commons.constants.BoardAuthority;
import org.koreait.commons.exceptions.AlertBackException;
import org.koreait.commons.exceptions.AlertException;
import org.koreait.entities.Board;
import org.koreait.entities.BoardData;
import org.koreait.entities.FileInfo;
import org.koreait.models.board.*;
import org.koreait.models.board.config.BoardConfigInfoService;
import org.koreait.models.board.config.BoardNotFoundException;
import org.koreait.models.file.FileInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController implements ScriptExceptionProcess { // 게시판 추가 시작 // 게시판 id가 없을때 뒤로가기

    private final Utils utils; // 회원 접근권한을 처리하기 위해
    private final MemberUtil memberUtil;

    private final BoardSaveService saveService; // saveservice작성후 연동을위해 의존성 주입
    private final BoardInfoService infoService;
    private final BoardConfigInfoService configInfoService; // 12-02추가 (게시판 설정부분 필요(공통 프로세스))
    private final FileInfoService fileInfoService;
    private final BoardDeleteService deleteService;

    private BoardData boardData;

    @GetMapping("/write/{bId}") // 최신버전은 @PathVariable에 (bId)항목 명시를 해줘야함
    public String write(@PathVariable String bId, @ModelAttribute BoardForm form, Model model) { // 필수적으로 필요한 것은 게시판 id
        commonProcess(bId, "write", model);

        if (memberUtil.isLogin()) {
            form.setPoster(memberUtil.getMember().getUserNm()); // 게시글 작성 할 때는 로그인 한 사용자의 이름으로 대체
        }

        form.setBId(bId); // 이후 _form.html이동

        return utils.tpl("board/write"); // 모바일도 알아서 연동할 수 있게 템플릿 연동
    }

    @GetMapping("/update/{seq}") // 게시글 번호
    public String update(@PathVariable Long seq, Model model) {
        if (!infoService.isMine(seq)) { // 직접 작성한 게시물이 아닌 경우
            throw new AlertBackException(Utils.getMessage("작성한_게시글만_수정할_수_있습니다.", "error"));
        }

        BoardForm form = infoService.getForm(seq);
        commonProcess(form.getBId(), "update", model); // 게시판 아이디는 위의 getForm(seq)에서 가져오고

        model.addAttribute("boardForm", form);

        return utils.tpl("board/update");
    }

    @PostMapping("/save")
    public String save(@Valid BoardForm form, Errors errors, Model model) {

        String mode = Objects.requireNonNullElse(form.getMode(), "write"); // null 관련 오류가 날수있으므로 objects
        String bId = form.getBId();

        commonProcess(bId, mode, model);

        if (mode.equals("update")) { // 본인이 쓴 것만 수정 가능하게
            Long seq = form.getSeq();
            if (!infoService.isMine(seq)) { // 직접 작성한 게시물이 아닌 경우
                throw new AlertBackException(Utils.getMessage("작성한_게시글만_수정할_수_있습니다.", "error"));
            }
        }

        saveService.save(form, errors);

        if (errors.hasErrors()) {
            String gid = form.getGid();
            List<FileInfo> editorImages = fileInfoService.getListAll(gid, "editor"); // 완료된파일, 미 완된파일 다 가져오기(수정하기 눌렀을 때 유지)
            List<FileInfo> attachFiles = fileInfoService.getListAll(gid, "attach");

            form.setEditorImages(editorImages);
            form.setAttachFiles(attachFiles);

            return utils.tpl("board/" + mode);
        }


        return "redirect:/board/list/" + bId;
    }

    @GetMapping("/view/{seq}")
    public String view(@PathVariable Long seq, @ModelAttribute BoardDataSearch search, Model model) {

        BoardData data = infoService.get(seq);
        boardData = data;

        // _list.html 작성후 이동
        String bId = data.getBoard().getBId();
        commonProcess(data.getBoard().getBId(), "view", model);

        search.setBId(bId);
        ListData<BoardData> listData = infoService.getList(search);

        model.addAttribute("boardData", data);
        model.addAttribute("items", listData.getContent());
        model.addAttribute("pagination", listData.getPagination());

        return utils.tpl("board/view");
    }

    @GetMapping("/delete/{seq}")
    public String delete(@PathVariable("seq") Long seq) {
        if (!infoService.isMine(seq)) {
            throw new AlertBackException(Utils.getMessage("작성한_게시글만_삭제가능합니다.", "error"));
        }

        BoardData data = infoService.get(seq);

        deleteService.delete(seq);

        return "redirect:/board/list/" + data.getBoard().getBId();
    }

    // 12-02 추가
    @GetMapping("/list/{bId}")
    public String list(@PathVariable("bId") String bId, @ModelAttribute BoardDataSearch search, Model model) { //@PathVariable("bId") 버전이 상승되면 옆에 명시
        // 12-09 16:09 추가
        commonProcess(bId, "list", model);

        search.setBId(bId);

        ListData<BoardData> data = infoService.getList(search);
        model.addAttribute("items", data.getContent());
        model.addAttribute("pagination", data.getPagination()); // 이후 list.html로 이동

        return utils.tpl("board/list"); // -> 템플릿으로(list.html로 이동)
    }

    // 12-09 15:16
    @PostMapping("/guest/password") // 최신버전은 추가 (@RequestParam("password") String password, HttpSession session, Model model)
    public String guestPasswordCheck(String password, HttpSession session, Model model) {
        Long seq = (Long)session.getAttribute("guest_seq");
        if (seq == null) {
            throw new BoardDataNotFoundException();
        }

        if (!infoService.checkGuestPassword(seq, password)) { // 비밀번호 검증 실패시
            throw new AlertException(Utils.getMessage("비밀번호가_일치하지_않습니다.", "error"));

        }

        // 검증 성공시
        String key = "chk_" + seq;
        session.setAttribute(key, true);
        session.removeAttribute("guest_seq");

        model.addAttribute("script", "parent.location.reload()");

        return "common/_execute_script";
    }

    private void commonProcess(String bId, String mode, Model model) {

        Board board = configInfoService.get(bId);

        if (board == null || (!board.isActive() && !memberUtil.isAdmin())) { // 등록되지 않거나 미사용 중 게시판 (관리자는 볼 수 있음)
            throw new BoardNotFoundException();
        }

        /* 게시판 분류 S */
        String category = board.getCategory();
        List<String> categories = StringUtils.hasText(category) ?
                Arrays.stream(category.trim().split("\\n"))
                        .map(s -> s.replaceAll("\\r", ""))
                        .toList()
                : null;

        model.addAttribute("categories", categories);
        /* 게시판 분류 E */ // 추가 이후 _form.html로 이동

        String bName = board.getBName();
        String pageTitle = bName; // 기본값, 반복이 되므로 위에 정의
        if (mode.equals("write")) pageTitle = bName + " 작성";
        else if (mode.equals("update")) pageTitle = bName + " 수정";
        else if (mode.equals("view") && boardData != null) {
            pageTitle = boardData.getSubject() + "||" + bName;
        }


        /* 글쓰기, 수정시 권한 체크 S 12-02 */
        if (mode.equals("write") || mode.equals("update")) {

            BoardAuthority authority = board.getAuthority();
            if (!memberUtil.isAdmin() && !memberUtil.isLogin() && authority == BoardAuthority.MEMBER) { // 회원 전용
                throw new AlertBackException(Utils.getMessage("MemberOnly.board", "error")); // 회원만 접근가능한 게시판
            }

            if (authority == BoardAuthority.ADMIN && !memberUtil.isAdmin()) { // 관리자 전용
                throw new AlertBackException(Utils.getMessage("AdminOnly.board", "error"));
            }
        }
        /* 글쓰기, 수정시 권한 체크 E */

        // 공통 스크립트 처리
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();
        if (mode.equals("write") || mode.equals("update")) { // 게시글 작성과 수정일 때
            addCommonScript.add("ckeditor/ckeditor");
            addCommonScript.add("fileManager");

            addScript.add("board/form");
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("pageTitle", pageTitle);
        // ---pageTitle설정
        model.addAttribute("board", board);

        //ckeditor5다운 후 resources -> common -> js에 붙여넣기 -> ckeditor안에 ckeditor.js를 추가해야함 -> front디렉토리 -> js에 board디렉토리생성
        // board디렉토리에 form.js생성
    }

    @ExceptionHandler(RequiredPasswordCheckException.class)
    public String guestPassword() {

        return utils.tpl("board/password");
    }
}
