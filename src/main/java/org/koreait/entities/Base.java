package org.koreait.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class) // 엔티티의 변화를 감지하는 리스너 -> MvcConfig에서 @EnableJpaAuditing추가
@EnableScheduling // 스케줄
public abstract class Base { // 공통적인 속성이나 기능을 공유할 때

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdAt; // 등록일자

    @Column(insertable = false)
    @LastModifiedDate
    private LocalDateTime modifiedAt; // 수정일자
}
