package org.koreait.controllers.admins;

import lombok.Data;

import java.util.List;

@Data
public class BoardSearch {
    private int page = 1;
    private int limit = 20; // 게시글 몇개씩 보기 ex) 20개씩 보기

    // 검색에 필요한 항목들을 커맨드객체로
    private String sopt;
    private String skey; // 뷰에서 작성한 키워드
    private List<Boolean> active;
    private List<String> authority;
    // -> boardController로 이동
}
