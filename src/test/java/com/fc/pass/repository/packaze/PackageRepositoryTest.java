package com.fc.pass.repository.packaze;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
class PackageRepositoryTest {
    @Autowired
    private PackageRepository packageRepository;

    @Test
    public void test_save(){
        // Given
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPackageName("바디 챌린지 PT 12주");
        packageEntity.setPackageSeq(84);
        // When
        packageRepository.save(packageEntity); // 저장
        // Then
        assertNotNull(packageEntity.getPackageSeq()); // null 이 아닌지 확인
    }

    @Test
    public void test_findByCreatedAtAfter(){
        // Given
        LocalDateTime dateTime = LocalDateTime.now().minusMinutes(1); // 지금 시점에서 1분전

        PackageEntity packageEntity0 = new PackageEntity();
        packageEntity0.setPackageName("학생 전용 3개월");
        packageEntity0.setPeriod(90);
        packageRepository.save(packageEntity0);

        PackageEntity packageEntity1 = new PackageEntity();
        packageEntity1.setPackageName("학생 전용 6 개월");
        packageEntity1.setPeriod(180);
        packageRepository.save(packageEntity1);
        // When
        // 차례대로 들어감 나오는건 PackageEntity1
        final List<PackageEntity> packageEntities = packageRepository.findByCreatedAtAfter(dateTime, PageRequest.of(0,1, Sort.by("packageSeq").descending()));
        // Then
        assertEquals(1,packageEntities.size());
        assertEquals(packageEntity1.getPackageSeq(),packageEntities.get(0).getPackageSeq());
    }

    @Test
    public void test_updateCountAndPeriod(){
        // Given
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPackageName("바디프로필 이벤트 4개월");
        packageEntity.setPeriod(90);
        packageRepository.save(packageEntity);
        // When
        int updatedCount = packageRepository.updateCountAndPeriod(packageEntity.getPackageSeq(),30,120);
        final PackageEntity updatedPackageEntity = packageRepository.findById(packageEntity.getPackageSeq()).get();
        // Then
        assertEquals(1, updatedCount);
        assertEquals(30, updatedPackageEntity.getCount());
        assertEquals(120, updatedPackageEntity.getPeriod());
    }

    @Test
    public void test_Delete(){
        // Given
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPackageName("");
        packageEntity.setCount(1);
        PackageEntity newPackageEntity = packageRepository.save(packageEntity);
        // When
        packageRepository.deleteById(newPackageEntity.getPackageSeq());
        // Then
        assertTrue(packageRepository.findById(newPackageEntity.getPackageSeq()).isEmpty());
    }
}