/**
 * 
 */
package com.x2bee.batch.app.jobconfig.sample;

import java.util.Arrays;

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
public class SampleProcessorConfig {
    private final JobBuilderFactory jobBuilderFactory; // Job 빌더 생성용
    private final StepBuilderFactory stepBuilderFactory; // Step 빌더 생성용

    // JobBuilderFactory를 통해서 Job을 생성
    @Bean
    public Job sampleProcessorJob() {
        return jobBuilderFactory.get("sampleProcessorJob")
                .start(sampleProcessorStep())  // Step 설정
                .incrementer(new UniqueRunIdIncrementer()) // 중복실행허용
                .build();
    }

    // StepBuilderFactory를 통해서 Step을 생성
    @Bean
    public Step sampleProcessorStep() {
        return stepBuilderFactory.get("sampleProcessorStep")
                .<Integer, Integer>chunk(3)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .faultTolerant()
                .skip(IllegalArgumentException.class)
                .skipLimit(3)
                .build();
    }

    @Bean
    public ItemReader<Integer> itemReader() {
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
    public ItemProcessor<Integer, Integer> itemProcessor() {
        return item -> {
            if (item.equals(5)) {
                log.info("Throwing exception on item " + item);
                throw new IllegalArgumentException("Unable to process 5");
            }
            log.info("processing item = " + item);
            return item;
        };
    }

    @Bean
    public ItemWriter<Integer> itemWriter() {
        return items -> {
            log.info("About to write chunk: " + items);
            for (Integer item : items) {
                log.info("writing item = " + item);
            }
        };
    }
    
}
