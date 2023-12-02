package org.koreait.controllers.members;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.koreait.commons.CommonProcess;
import org.koreait.commons.MemberUtil;
import org.koreait.commons.Utils;
import org.koreait.entities.Member;
import org.koreait.models.member.MemberInfo;
import org.koreait.models.member.MemberSaveService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController implements CommonProcess {

    private final Utils utils;

    private final MemberSaveService saveService;

    @GetMapping("/join")
    public String join(@ModelAttribute RequestJoin form, Model model) {
        commonProcess(model, Utils.getMessage("회원가입", "common")); // code추가

        return utils.tpl("member/join"); // 모바일, PC가 자동 인식
    }

    // 11-21추가
    @PostMapping("join")
    public String joinPs(@Valid RequestJoin form, Errors errors, Model model) { // 커맨드 객체 검증 // errors객체는 커맨드객체 다음에
        commonProcess(model, Utils.getMessage("회원가입", "common"));

        saveService.join(form, errors);

        if (errors.hasErrors()) {
            return utils.tpl("member/join");
        } // 검증(실패시 유입)

        // 가입성공시
        return "redirect:/member/login";
    }

    @GetMapping("/login") // @RequestParam(name = "redirectURL", required = false) String redirectURL, Model model => 버전업시 매개변수 이걸로 변경
    public String login(String redirectURL, Model model) { // post는 security가 알아서 처리
        commonProcess(model, Utils.getMessage("로그인", "common"));

        model.addAttribute("redirectURL", redirectURL);

        return utils.tpl("member/login");
    }

//    @ResponseBody
//    @GetMapping("/info")
//    public void info() {
//
//        Member member = memberUtil.getMember();
//        if (memberUtil.isLogin()) {
//            log.info(member.toString());
//        }
//        log.info("로그인 여부 : {}", memberUtil.isLogin());
//    }
    /*
    public void info() {
        MemberInfo member = (MemberInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info(member.toString());
    }
     */
    /*
    public void info(@AuthenticationPrincipal MemberInfo memberInfo) {
        log.info(memberInfo.toString());
    }
     */
    /*
    public void info(Principal principal) { // 로그인 했을 때 로그인 정보가 담김(ID만)
        String email = principal.getName();
        log.info(email);
    }
     */
}
