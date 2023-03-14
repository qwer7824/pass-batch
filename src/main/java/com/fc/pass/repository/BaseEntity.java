package com.fc.pass.repository;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
@MappedSuperclass // 엔티티 클래스가 일반 클래스를 상속하기 위해서는 일반 클래스에 붙이는 에노테이션
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedDate // 생성되어 저장될 때의 시간을 자동으로 저장한다.
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate // 조회한 엔티티 값을 변경할 때 시간을 자동으로 저장한다.
    private LocalDateTime modifiedAt;
}
