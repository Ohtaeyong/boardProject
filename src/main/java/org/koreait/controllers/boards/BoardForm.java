package org.koreait.controllers.boards;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.koreait.entities.FileInfo;

import java.util.List;
import java.util.UUID;

@Data
public class BoardForm {

    private String mode = "write"; // 기본값은 write로

    private Long seq; // 수정할 때 필요

    private String bId; // 게시판 id

    private String gid = UUID.randomUUID().toString(); // 추가 12-02

    private String category;

    @NotBlank(message = "제목을 입력하세요")
    private String subject;

    @NotBlank(message = "작성자를 입력하세요")
    private String poster;

    @NotBlank(message = "내용을 입력하세요")
    private String content;

    // 데이터 접근하려면 repository로

    private boolean notice; // 12-02 추가

    private String guestPw;

    private List<FileInfo> editorImages;
    private List<FileInfo> attachFiles;

}
