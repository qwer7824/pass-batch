package com.fc.pass.repository.pass;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BulkPassRepository extends JpaRepository<BulkPassEntity, Integer> {
    // WHERE status = :status AND startedAt > :startedAt
    // status 가 p.status 이고 startedAt이 p.startedAt 보다 크다면 대상이 된다.
    List<BulkPassEntity> findByStatusAndStartedAtGreaterThan(BulkPassStatus status , LocalDateTime startedAt);
}
