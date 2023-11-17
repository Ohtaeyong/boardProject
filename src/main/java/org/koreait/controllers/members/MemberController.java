package org.koreait.controllers.members;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.koreait.commons.MemberUtil;
import org.koreait.commons.Utils;
import org.koreait.commons.constants.MemberType;
import org.koreait.entities.BoardData;
import org.koreait.entities.Member;
import org.koreait.models.member.MemberInfo;
import org.koreait.repositories.BoardDataRepository;
import org.koreait.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Transactional
public class MemberController {

    private final Utils utils;
    private final MemberUtil memberUtil;
    private final EntityManager em;

    @Autowired
    private BoardDataRepository boardDataRepository;

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/join")
    public String join() {

        return utils.tpl("member/join");
    }

    @GetMapping("/login")
    public String login(String redirectURL, Model model) {

        model.addAttribute("redirectURL", redirectURL);

        return utils.tpl("member/login");
    }

    @ResponseBody
    @GetMapping("/info")
    public void info() {
        Member member = Member.builder()
                .email("user01@test.org")
                .password("123456")
                .userNm("사용자01")
                .mtype(MemberType.USER)
                .build();
        memberRepository.saveAndFlush(member);

        BoardData item = BoardData.builder()
                .subject("제목")
                .content("내용")
                .member(member)
                .build();
        boardDataRepository.saveAndFlush(item);

        em.clear();

        BoardData data = boardDataRepository.findById(1L).orElse(null);

        Member member2 = data.getMember();
        String email = member2.getEmail(); // 2차 쿼리 실행
        System.out.println(email);

    }

    @ResponseBody
    @GetMapping("/info2")
    public void info2() {
        Member member = Member.builder()
                .email("user01@test.org")
                .password("123456")
                .userNm("사용자01")
                .mtype(MemberType.USER)
                .build();
        memberRepository.saveAndFlush(member);

        List<BoardData> items = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            BoardData item = BoardData.builder()
                    .subject("제목")
                    .content("내용")
                    .member(member)
                    .build();
            items.add(item);
        }

        boardDataRepository.saveAllAndFlush(items);
    }

    @ResponseBody
    @GetMapping("/info3")
    public void info3() {
        List<BoardData> items = boardDataRepository.findAll();
        for (BoardData item : items) {
            Member member = item.getMember();
            String email = member.getEmail();
            System.out.println(email);
        }
    }
}