package org.koreait.controllers.boards;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.commons.MemberUtil;
import org.koreait.commons.ScriptExceptionProcess;
import org.koreait.commons.Utils;
import org.koreait.entities.BoardData;
import org.koreait.models.board.BoardInfoService;
import org.koreait.models.board.BoardSaveService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @GetMapping("/write/{bId}") // 최신버전은 @PathVariable에 (bId)항목 명시를 해줘야함
    public String write(@PathVariable String bId, @ModelAttribute BoardForm form, Model model) { // 필수적으로 필요한 것은 게시판 id
        commonProcess(bId, "write", model);

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
    public String delete(@PathVariable Long seq) {

        return "redirect:/board/list/게시판 ID";
    }

    private void commonProcess(String bId, String mode, Model model) {

        String pageTitle = "게시글 목록"; // 기본값
        if (mode.equals("write")) pageTitle = "게시글 작성";
        else if (mode.equals("update")) pageTitle = "게시글 수정";
        else if (mode.equals("view")) pageTitle = "게시글 제목";

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
