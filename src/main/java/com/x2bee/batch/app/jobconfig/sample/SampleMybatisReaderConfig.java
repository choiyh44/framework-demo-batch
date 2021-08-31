/**
 * 
 */
package com.x2bee.batch.app.jobconfig.sample;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
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
public class SampleMybatisReaderConfig {
    private final JobBuilderFactory jobBuilderFactory; // Job 빌더 생성용
    private final StepBuilderFactory stepBuilderFactory; // Step 빌더 생성용
    private final SqlSessionFactory sqlSessionFactory;
    
    private static final int CHUNK_SIZE = 3;
    
    // JobBuilderFactory를 통해서 Job을 생성
    @Bean
    public Job sampleMybatisReaderJob() throws Exception {
        return jobBuilderFactory.get("sampleMybatisReaderJob")
                .start(sampleMybatisReaderStep())  // Step 설정
                .incrementer(new UniqueRunIdIncrementer()) // 중복실행허용
                .build();
    }

    // StepBuilderFactory를 통해서 Step을 생성
    @Bean
    public Step sampleMybatisReaderStep() throws Exception {
        return stepBuilderFactory.get("sampleMybatisReaderStep")
                .<Sample, String>chunk(CHUNK_SIZE)
                .reader(myBatisItemReader())
                .processor(myBatisItemProcessor())
                .writer(myBatisItemWriter())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(3)
                .build();
    }

    @Bean
    //@StepScope
    public MyBatisPagingItemReader<Sample> myBatisItemReader() throws Exception {
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("id", 10);
        return new MyBatisPagingItemReaderBuilder<Sample>()
                .pageSize(CHUNK_SIZE)
                .sqlSessionFactory(sqlSessionFactory)
                .queryId("com.x2bee.batch.app.repository.sample.SampleMapper.getSamplePagingList")
                .parameterValues(parameterValues)
                .build();
    }
    
    @Bean
    public ItemProcessor<Sample, String> myBatisItemProcessor() {
        return item -> {
            String str = item.toString();
            log.info("processing item = " + str);
            return str;
        };
    }

    @Bean
    public ItemWriter<String> myBatisItemWriter() {
        return items -> {
            log.info("About to write chunk: " + items);
            for (String item : items) {
                log.info("writing item = " + item);
            }
        };
    }
    
}

