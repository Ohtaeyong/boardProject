package org.koreait.configs;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.koreait.models.member.LoginFailureHandler;
import org.koreait.models.member.LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties(FileUploadConfig.class)
public class SecurityConfig {

    @Autowired
    private FileUploadConfig fileUploadConfig;

    // 인증 설정 로그인 S
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

        // 로그아웃 기능 11-14
        http.logout(c -> {
           c.logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                   .logoutSuccessUrl("/member/login"); // 로그아웃 성공시 이동될 페이지
        });
        // 인증 설정 - 로그인 E

        // 헤더설정
        http.headers(c -> { // 같은 출처일 때 허용(iframe)
           c.frameOptions(o -> o.sameOrigin());
        });

        /* 인가 설정 - 접근 통제 S */
        http.authorizeHttpRequests(c -> {
           c.requestMatchers("/mypage/**").authenticated() // 회원 전용(로그인한 회원만 접근 가능)
                   //.requestMatchers("/admin/**").hasAuthority("ADMIN") // 관리자 권한만 접근
                   .requestMatchers( // 11-21 log의 warn 메시지로 인해 update
                           "/front/css/**",
                           "/front/js/**",
                           "/front/images/**",

                           "/mobile/css/**",
                           "/mobile/js/**",
                           "/mobile/images/**",

                           "/admin/css/**",
                           "/admin/js/**",
                           "/admin/images/**",

                           "/common/css/**",
                           "/common/js/**",
                           "/common/images/**",
                           fileUploadConfig.getUrl() + "**").permitAll()
                   .anyRequest().permitAll(); // 나머지 페이지는 권한 필요없음
        });

        // 상세 설정
        http.exceptionHandling(c -> {
           c.authenticationEntryPoint((req, resp, e) -> {
              String URI = req.getRequestURI();
              if (URI.indexOf("/admin") != -1) { // 관리자 페이지 - 401 응답 코드
                  resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "NOT AUTHORIZED");
              } else { // 회원전용 페이지(예 - /mypage) -> 로그인 페이지 이동
                  String url = req.getContextPath() + "/member/login";
                  resp.sendRedirect(url);
              }
           });
        });
        /* 인가 설정 - 접근 통제 E */

        return http.build(); // 설정 무력화 (서버켰을시 로그인화면 X)
    }


//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        // 시큐리티 설정이 적용될 필요가 없는 경로 설정
//
//        return w -> w.ignoring().requestMatchers(
//                );
//
//    } 11-21 제거

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
