package org.koreait.controllers.admins;

import lombok.Data;

@Data
public class BoardSearch {
    private int page = 1;
    private int limit = 20; // 게시글 몇개씩 보기 ex) 20개씩 보기
}
