package org.koreait.configs;

import org.koreait.models.member.LoginFailureHandler;
import org.koreait.models.member.LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.formLogin(f -> {
            f.loginPage("/member/login")
                    .usernameParameter("email") // 알려주면 찾아서 가져옴
                    .passwordParameter("password")
                    //.successForwardUrl("/") // 로그인 성공했을 때 이동할 경로
                    .successHandler(new LoginSuccessHandler()) // 위에 것 보다 이걸 쓰는게 더 낫다 (뭐가 오류인지 모르기 때문)
                    //.failureUrl("/member/login?error=true"); // 로그인 실패했을 때
                    .failureHandler(new LoginFailureHandler()); // 위와 동일 -> ()정보를 가지고 상세하게 설정
        }); // DSL

        return http.build(); // 설정 무력화 (서버켰을시 로그인화면 X)
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
