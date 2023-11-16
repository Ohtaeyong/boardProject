package org.koreait.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class BoardView {

    @Id
    private Long seq;

    @Id
    @Column(name = "_uid")
    private Integer uid; // uid가 예약어라서 오류발생 @Id추가
}
