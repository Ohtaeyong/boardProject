package org.koreait.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor @AllArgsConstructor
public class BoardData extends BaseMember {

    @Id @GeneratedValue
    private Long seq; // 게시글 번호

    @Column(length = 100, nullable = false) // 100자, 필수항목
    private String subject;

    @Lob
    @Column(nullable = false)
    private String content;


}
