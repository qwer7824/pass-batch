package com.fc.pass.job.pass;

import com.fc.pass.config.TestBatchConfig;
import com.fc.pass.repository.pass.PassEntity;
import com.fc.pass.repository.pass.PassRepository;
import com.fc.pass.repository.pass.PassStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {ExpirePassesJobConfig.class, TestBatchConfig.class})
public class ExpirePassesJobConfigTest {
    @Autowired //CLI 등으로 실행하는 Job을 테스트 코드에서 Job을 실행할 수 있도록 지원합니다.
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private PassRepository passRepository;

    @Test
    public void test_expirePassesStep() throws Exception {
        // given
        addPassEntities(10); // 랜덤값 10개 생성

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(); // launchJob 가져옴
        JobInstance jobInstance = jobExecution.getJobInstance(); // getJobInstance 가져옴

        // then
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus()); // COMPLETED 검증
        assertEquals("expirePassesJob", jobInstance.getJobName()); // 동작되고있는 expirePassesJob 검증

    }

    private void addPassEntities(int size) {
        final LocalDateTime now = LocalDateTime.now();
        final Random random = new Random();

        List<PassEntity> passEntities = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            PassEntity passEntity = new PassEntity();
            passEntity.setPackageSeq(1);
            passEntity.setUserId("A" + 1000000 + i);
            passEntity.setStatus(PassStatus.PROGRESSED);
            passEntity.setRemainingCount(random.nextInt(11));
            passEntity.setStartedAt(now.minusDays(60));
            passEntity.setEndedAt(now.minusDays(1));
            passEntities.add(passEntity);

        }
        passRepository.saveAll(passEntities);

    }

}