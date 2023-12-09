package org.koreait.controllers.boards;

import lombok.Data;

@Data // 커맨드 객체이므로
public class BoardDataSearch {
    private String bId; // 게시판 id
    private int page = 1; // 기본값은 1페이지
    private int limit = 20; // 한 페이지에 스무개씩
    // 제목, 제목 + 내용, 작성자명, 작성자 + 아이디
    private String category; // 검색을 위해 추가
    private String sopt; // 검색 옵션
    private String skey; // 검색 키워드
}
