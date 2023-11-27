package org.koreait.controllers.admins;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.commons.ListData;
import org.koreait.commons.ScriptExceptionProcess;
import org.koreait.commons.constants.BoardAuthority;
import org.koreait.commons.menus.Menu;
import org.koreait.entities.Board;
import org.koreait.models.board.config.BoardConfigInfoService;
import org.koreait.models.board.config.BoardConfigSaveService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller("adminBoardController") // 이름이 겹칠 수 있다
@RequestMapping("/admin/board") // 주소는 어떻게 할 것인지
@RequiredArgsConstructor
public class BoardController implements ScriptExceptionProcess {

    private final HttpServletRequest request; // menu의 가장 아랫부분을 가져와야함

    private final BoardConfigSaveService saveService;

    private final BoardConfigInfoService infoService;

    @GetMapping // 따로 추가하지 않을 시 ("/admin/board")
    public String list(@ModelAttribute BoardSearch search, Model model) {
        commonProcess("list", model);

        ListData<Board> data = infoService.getList(search); // 연동

        model.addAttribute("items", data.getContent());
        model.addAttribute("pagination", data.getPagination()); // -> 템플릿으로 (list.html)

        return "admin/board/list";
    }

    @GetMapping("/add")
    public String register(@ModelAttribute BoardConfigForm form, Model model) { // 등록
        commonProcess("add", model);

        return "admin/board/add";
    }

    @GetMapping("/edit/{bId}")
    public String update(@PathVariable String bId, Model model) { // 새로운 버전에서는 @PathVariable("bId") ()정의를 해줘야함
        commonProcess("edit", model);

        return "admin/board/edit";
    }

    // 게시판 설정을 저장하는 save
    @PostMapping("/save")
    public String save(@Valid BoardConfigForm form, Errors errors, Model model) {
        // mode값에 따라서 구분
        String mode = Objects.requireNonNullElse(form.getMode(), "add"); // 11-24 테스트 수정
        commonProcess(mode, model);

        if (errors.hasErrors()) {
            return "admin/board/" + mode;
        }

        saveService.save(form);

        return "redirect:/admin/board";
    }

    private void commonProcess(String mode, Model model) {
        String pageTitle = "게시판 목록"; // mode의 기본값
        mode = Objects.requireNonNullElse(mode, "list"); // null값이면 기본값은 list
        if (mode.equals("add")) pageTitle = "게시판 등록";
        else if (mode.equals("edit")) pageTitle = "게시판 수정";

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("menuCode", "board");
        model.addAttribute("submenus", Menu.gets("board"));
        // 서브메뉴코드 불러오기
        model.addAttribute("subMenuCode", Menu.getSubMenuCode(request));

        // enum클래스 작성 후 추가
        model.addAttribute("authorities", BoardAuthority.getList()); // list.html로 이동
    }
}
