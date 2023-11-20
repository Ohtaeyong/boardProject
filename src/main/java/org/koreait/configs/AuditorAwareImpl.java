package org.koreait.configs;

import org.koreait.models.member.MemberInfo;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component // 스프링 관리 객체
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        String email = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof MemberInfo) {
            MemberInfo memberInfo = (MemberInfo)auth.getPrincipal();
            email = memberInfo.getEmail();
        }

        return Optional.ofNullable(email);
        // 자동적으로 로그인한 정보가 회원테이블에 추가
    }
}
