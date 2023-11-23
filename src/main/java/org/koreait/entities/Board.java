package org.koreait.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.koreait.commons.constants.BoardAuthority;

@Entity
@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class Board extends BaseMember { // 게시판 설정 // 누가 만들었는지 알수 있음(BaseMember)

    @Id
    @Column(length = 30)
    private String bId;

    @Column(length = 60, nullable = false) // 필수 항목
    private String bName; // 게시판 명

    private boolean active; // 사용여부

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private BoardAuthority authority = BoardAuthority.ALL; // 권한과 기본값

    @Lob
    private String category;
}
