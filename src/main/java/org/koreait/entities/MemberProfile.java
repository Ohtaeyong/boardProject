package org.koreait.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity @Data
public class MemberProfile { // OneToOne : 일대일

    @Id @GeneratedValue
    private Long Seq;

    @Column(length = 100)
    private String image;

    @ToString.Exclude
    @OneToOne(mappedBy = "profile") // 관계의 주인 명시
    private Member member;
}
