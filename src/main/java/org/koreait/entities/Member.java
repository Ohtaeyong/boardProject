package org.koreait.entities;

import lombok.Data;
import org.koreait.commons.constants.MemberType;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
public class Member {

    @Id
    private Long userNo;

    private String email;

    private String password;

    private String userNm;

    private String mobile;

    private MemberType mtype = MemberType.USER; // 기본타입은 USER

    private LocalDateTime regDt;

    private LocalDateTime modDt;
}
