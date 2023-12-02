package org.koreait.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data @Builder
@NoArgsConstructor @AllArgsConstructor
@Table(indexes = { // board_data = bd
        @Index(name = "idx_bd_list", columnList = "notice DESC, createdAt DESC"),
        @Index(name = "idx_bd_category", columnList = "category")
}) // 정렬순서 (12-02)
public class BoardData extends Base { // BoardData가 many

    @Id @GeneratedValue
    private Long seq; // 게시글 번호

    @Column(length = 45, nullable = false)
    private String gid = UUID.randomUUID().toString(); // 그룹아이디 추가 12-02

    // 12-02 추가 (게시글 작성)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bId")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo")
    private Member member;

    @Column(length = 50)
    private String category; // 12-02

    @Column(length = 30, nullable = false)
    private String poster;

    @Column(length = 65)
    private String guestPw; // 비회원 비밀번호 12-02추가

    @Column(nullable = false)
    private String subject;

    @Lob
    @Column(nullable = false)
    private String content;

    private boolean notice; // 공지사항 여부 (보통 1이 우선순위)

    @Transient
    private List<FileInfo> editorImages;

    @Transient
    private List<FileInfo> attachFiles;
}
