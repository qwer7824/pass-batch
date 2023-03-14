package com.fc.pass.job.pass;

import com.fc.pass.repository.pass.PassEntity;
import com.fc.pass.repository.pass.PassStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class ExpirePassesJobConfig {

    private final int CHUNK_SIZE = 5;

    // @EnableBatchProcessing 로 인해 Bean 으로 제공된 JobBuilderFactory,StepBuilderFactory

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final EntityManagerFactory entityManagerFactory;

    public ExpirePassesJobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, EntityManagerFactory entityManagerFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public Job expirePassesJob(){
    return this.jobBuilderFactory.get("expirePassesJob") //ExpirePassesJob 이라는 잡 만듬
            .start(expirePassesStep())
            .build();
    }

    @Bean
    public Step expirePassesStep(){
    return this.stepBuilderFactory.get("expirePassesStep")
            .<PassEntity, PassEntity>chunk(CHUNK_SIZE) // input , output 으로 선언한 걸로 되어있음
            .reader(expirePassesItemReader())
            .processor(expirePassesItemProcessor())
            .writer(expirePassesItemWriter())
            .build();
    }

    /**
     * JpaCursorItemReader : JpaCursorItemReader 만 지원하다가 스프링 4.3에서 추가 되었음.
     * 페이징 기법보다 높은 성능으로 , 데이터 변경에 무관한 무결성 조회가 가능함
     */
    @Bean
    @StepScope
    public JpaCursorItemReader<PassEntity> expirePassesItemReader() {
        return new JpaCursorItemReaderBuilder<PassEntity>()
                .name("expirePassesItemReader")
                .entityManagerFactory(entityManagerFactory)
                // 상태(status)가 진행중이며, 종료일시(endedAt)이 현재 시점보다 과거일 경우 만료 대상이 됩니다.
                .queryString("select p from PassEntity p where p.status = :status and p.endedAt <= :endedAt order by p.passSeq")
                .parameterValues(Map.of("status", PassStatus.PROGRESSED, "endedAt", LocalDateTime.now()))
                .build();
    }

    @Bean
    public ItemProcessor<PassEntity, PassEntity> expirePassesItemProcessor() {
        return passEntity -> {
            passEntity.setStatus(PassStatus.EXPIRED);
            passEntity.setExpiredAt(LocalDateTime.now());
            return passEntity;
        };
    }

    /**
     * JpaItemWriter: JPA의 영속성 관리를 위해 EntityManager를 필수로 설정해줘야 합니다.
     */
    @Bean
    public JpaItemWriter<PassEntity> expirePassesItemWriter() {
        return new JpaItemWriterBuilder<PassEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

}

