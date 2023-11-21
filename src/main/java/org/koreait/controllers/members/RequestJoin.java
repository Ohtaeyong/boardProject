package org.koreait.controllers.members;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestJoin {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8) // 비밀번호 자릿수 추가 -> validations.properties에서 메시지 추가
    private String password;

    @NotBlank
    private String confirmPassword;

    @NotBlank
    private String userNm;

    private String mobile;

    @AssertTrue
    boolean agree;
}