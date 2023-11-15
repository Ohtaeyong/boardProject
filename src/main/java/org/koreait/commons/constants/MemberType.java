package org.koreait.commons.constants;

public enum MemberType {
    // @Enumerated -> 만일 이상태에서 USER와 ADMIN의 순서가 바뀐다면 일반회원이 관리자가 되는 대참사가 발생 순서가 바뀌어도 상관없도록

    USER, // 일반 회원

    ADMIN // 관리자
}
