package org.koreait.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.koreait.commons.constants.MemberType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor @AllArgsConstructor
@Table(indexes = {
        @Index(name = "idx_member_userNm", columnList = "userNm"),
        @Index(name = "idx_member_mobile", columnList = "mobile")
})
public class Member extends Base {

    @Id @GeneratedValue(strategy = GenerationType.AUTO) // 자동으로 시퀀스 객체 생성
    private Long userNo;

    @Column(length = 65, unique = true, nullable = false) // //최대 65자까지 입력 가능 //유니크 제약조건 // 필수 항목
    private String email;

    @Column(length = 65, name = "pw", nullable = false)
    private String password;

    @Column(length = 40, nullable = false)
    private String userNm;

    @Column(length = 11)
    private String mobile;

    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING) // 설명 Enum(MemberType)에
    private MemberType mtype = MemberType.USER; // 기본타입은 USER

    @ToString.Exclude
    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER) // 관계 주인 명시
    private List<BoardData> items = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "profile_seq")
    private MemberProfile profile;

//    @Lob
//    private String introduction; // ex 게시글

    // 11-16 Base에 있으므로 주석
//    @Column(updatable = false) // 한번 insert될 때 추가 되고 수정X
//    @CreationTimestamp // 표준은 아니라 안쓰게 될 것
//    private LocalDateTime regDt;
//
//    @Column(insertable = false) // 수정될 때만 추가
//    @UpdateTimestamp // update시에 쿼리 추가
//    private LocalDateTime modDt;

//    @Transient // DB에 반영되지 않음 // 내부에서 쓸 목적으로 사용
//    private String tmpData;

//    @Temporal()
//    private Date date;
}
