package org.koreait.models.member;

import lombok.RequiredArgsConstructor;
import org.koreait.commons.constants.MemberType;
import org.koreait.controllers.members.JoinValidator;
import org.koreait.controllers.members.RequestJoin;
import org.koreait.entities.Member;
import org.koreait.repositories.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
@RequiredArgsConstructor
public class MemberSaveService {
    private final MemberRepository repository;
    private final PasswordEncoder encoder;
    private final JoinValidator validator;

    /**
     * 회원가입 처리
     * @param form
     * @param errors
     */
    public Member join(RequestJoin form, Errors errors) { // 검증
        validator.validate(form, errors);

        if (errors.hasErrors()) {
            return null;
        } // 문제가 있을때는 멈춤 -> 검증실패

        String hash = encoder.encode(form.getPassword());

        Member member = Member.builder()
                .email(form.getEmail())
                .userNm(form.getUserNm())
                .mobile(form.getMobile())
                .mtype(MemberType.USER)
                .password(hash)
                .build();

        save(member);

        member = repository.findByEmail(form.getEmail()).get();
        return member;
    }

    public void save(Member member) {
        String mobile = member.getMobile();
        if (mobile != null) {
            mobile = mobile.replaceAll("\\D", "");
            member.setMobile(mobile);
        }

        repository.saveAndFlush(member);
    }

}
