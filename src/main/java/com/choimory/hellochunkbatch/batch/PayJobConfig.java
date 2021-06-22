package com.choimory.hellochunkbatch.batch;

import com.choimory.hellochunkbatch.pay.entity.Pay;
import com.choimory.hellochunkbatch.pay.entity.Pay2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Entity;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PayJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final DataSource dataSource;
    private static final int CHUNK_SIZE = 10;

    @Bean
    public Job jpaPagingJob(){
        return jobBuilderFactory.get("jpaPagingJob")
                .start(jpaPagingStep())
                .build();
    }

    @Bean
    public Step jpaPagingStep(){
        return stepBuilderFactory.get("jpaPagingStep")
                .<Pay, Pay2>chunk(CHUNK_SIZE)
                .reader(jpaPagingReader())
                .processor(jpaItemProcessor())
                .writer(jpaPagingWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Pay> jpaPagingReader(){
        return new JpaPagingItemReaderBuilder<Pay>().name("jpaPagingReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT p FROM Pay p ORDER BY id ASC")
                .build();
    }

    @Bean
    public ItemProcessor<Pay, Pay2> jpaItemProcessor(){
        return pay -> Pay2.builder()
                .amount(pay.getAmount())
                .txDateTime(pay.getTxDateTime())
                .txName(pay.getTxName())
                .build();
    }

    @Bean
    public JpaItemWriter<Pay2> jpaPagingWriter(){
        JpaItemWriter<Pay2> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}
