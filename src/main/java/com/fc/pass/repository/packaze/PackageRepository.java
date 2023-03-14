package com.fc.pass.repository.packaze;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface PackageRepository extends JpaRepository<PackageEntity, Integer> {
    List<PackageEntity> findByCreatedAtAfter(LocalDateTime dateTime, Pageable pageable);

    @Transactional // 메서드가 포함하고 있는 작업 중에 하나라도 실패할 경우 전체 작업을 취소한다.
    @Modifying // @Query 로 작성 된 변경, 삭제 쿼리 메서드를 사용할 때 필요합니다.
    @Query(value = "UPDATE PackageEntity p" +
            "           SET p.count = :count," +
            "              p.period = :period" +
            "        WHERE p.packageSeq = :packageSeq"
    ) // 실제 SQL문 구현 어노테이션 (@Param 을 붙여 줘야 에러가 안남)
    int updateCountAndPeriod(@Param("packageSeq")Integer packageSeq, @Param("count")Integer count, @Param("period")Integer period);
}
