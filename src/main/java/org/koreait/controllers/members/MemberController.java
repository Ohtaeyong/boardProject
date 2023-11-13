package org.koreait.controllers.members;

import lombok.RequiredArgsConstructor;
import org.koreait.commons.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final Utils utils;

    @GetMapping("/join")
    public String join() {
        return utils.tpl("member/join"); // 모바일, PC가 자동 인식
    }

    @GetMapping("/login")
    public String login(String redirectURL, Model model) { // post는 security가 알아서 처리

        model.addAttribute("redirectURL", redirectURL);

        return utils.tpl("member/login");
    }
}
