package org.koreait.controllers.boards;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BoardForm {

    private String mode;

    private Long seq; // 수정할 때 필요

    private String bId; // 게시판 id

    @NotBlank(message = "제목을 입력하세요")
    private String subject;

    @NotBlank(message = "작성자를 입력하세요")
    private String poster;

    @NotBlank(message = "내용을 입력하세요")
    private String content;

    // 데이터 접근하려면 repository로

}
