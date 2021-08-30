/**
 * 
 */
package com.x2bee.batch.app.jobconfig.sample;

import java.util.Arrays;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.x2bee.batch.app.entity.Sample;
import com.x2bee.batch.base.UniqueRunIdIncrementer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author choiyh44
 * @version 1.0
 * @since 2021. 8. 30.
 *
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SampleMybatisWriterConfig {
    private final JobBuilderFactory jobBuilderFactory; // Job 빌더 생성용
    private final StepBuilderFactory stepBuilderFactory; // Step 빌더 생성용
    private final SqlSessionFactory sqlSessionFactory;
    
    private static final int CHUNK_SIZE = 3;
    
    // JobBuilderFactory를 통해서 Job을 생성
    @Bean
    public Job sampleMybatisWriterJob() throws Exception {
        return jobBuilderFactory.get("sampleMybatisWriterJob")
                .start(sampleMybatisWriterStep())  // Step 설정
                .incrementer(new UniqueRunIdIncrementer()) // 중복실행허용
                .build();
    }

    // StepBuilderFactory를 통해서 Step을 생성
    @Bean
    public Step sampleMybatisWriterStep() throws Exception {
        return stepBuilderFactory.get("sampleMybatisWriterStep")
                .<Integer, Sample>chunk(CHUNK_SIZE)
                .reader(sampleMybatisWriterReader())
                .processor(sampleMybatisWriterProcessor())
                .writer(sampleMybatisWriterWriter())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(3)
                .build();
    }

    @Bean
    public ItemReader<Integer> sampleMybatisWriterReader() {
        return new ListItemReader<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6)) {
            @Override
            public Integer read() {
                Integer item = super.read();
                log.info("reading item = " + item);
                return item;
            }
        };
    }
    
    @Bean
    public ItemProcessor<Integer, Sample> sampleMybatisWriterProcessor() {
        return item -> {
            if (item.equals(5)) {
                log.info("Throwing exception on item " + item);
                throw new IllegalArgumentException("Unable to process 5");
            }
            Sample sample = new Sample();
            sample.setName("myBatisTestName" + item);
            sample.setDescription("myBatisTestDescription" + item);
            sample.setSysRegrId("BATCH");
            sample.setSysModrId("BATCH");
            log.info("processing item = " + sample);
            return sample;
        };
    }

    @Bean
    public ItemWriter<Sample> sampleMybatisWriterWriter() {
        return new MyBatisBatchItemWriterBuilder<Sample>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.x2bee.batch.app.repository.sample.SampleTrxMapper.insertSample")
                .build();
    }
    
}

