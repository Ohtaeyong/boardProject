package org.koreait.controllers.boards;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.commons.MemberUtil;
import org.koreait.commons.ScriptExceptionProcess;
import org.koreait.commons.Utils;
import org.koreait.commons.constants.BoardAuthority;
import org.koreait.commons.exceptions.AlertBackException;
import org.koreait.commons.exceptions.AlertException;
import org.koreait.entities.Board;
import org.koreait.entities.BoardData;
import org.koreait.models.board.BoardInfoService;
import org.koreait.models.board.BoardSaveService;
import org.koreait.models.board.config.BoardConfigInfoService;
import org.koreait.models.board.config.BoardNotFoundException;
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

        return utils.tpl("board/update");
    }

    @PostMapping("/save")
    public String save(@Valid BoardForm form, Errors errors, Model model) {

        String mode = Objects.requireNonNullElse(form.getMode(), "write"); // null 관련 오류가 날수있으므로 objects
        String bId = form.getBId();

        commonProcess(bId, mode, model);

        if (errors.hasErrors()) {
            return utils.tpl("board/" + mode);
        }

        saveService.save(form);

        return "redirect:/board/list/" + bId;
    }

    @GetMapping("/view/{seq}")
    public String view(@PathVariable Long seq, Model model) {

        BoardData data = infoService.get(seq);
        model.addAttribute("boardData", data);

        return utils.tpl("board/view");
    }

    @GetMapping("/delete/{seq}")
    public String delete(@PathVariable("seq") Long seq) {

        return "redirect:/board/list/게시판 ID";
    }

    // 12-02 추가
    @GetMapping("/list/{bId}")
    public String list(@PathVariable("bId") String bId, Model model) { //@PathVariable("bId") 버전이 상승되면 옆에 명시

        return utils.tpl("board/list"); // -> 템플릿으로(list.html로 이동)
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
        else if (mode.equals("view")) pageTitle = "게시글 제목";


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

        //ckeditor5다운 후 resources -> common -> js에 붙여넣기 -> ckeditor안에 ckeditor.js를 추가해야함 -> front디렉토리 -> js에 board디렉토리생성
        // board디렉토리에 form.js생성
    }
}
