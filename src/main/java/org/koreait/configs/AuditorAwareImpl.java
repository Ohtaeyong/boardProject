package org.koreait.configs;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); // getAuthentication -> 로그인한 회원정보를 담고있음
        Object principal = auth.getPrincipal();
        System.out.println("principal: " + principal);

        return Optional.empty();
    }
}
