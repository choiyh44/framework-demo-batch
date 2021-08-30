/**
 * 
 */
package com.x2bee.batch.app.jobconfig.sample;

import java.util.Arrays;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SampleTwoStepConfig {
    private final JobBuilderFactory jobBuilderFactory; // Job 빌더 생성용
    private final StepBuilderFactory stepBuilderFactory; // Step 빌더 생성용
    
    private static final int CHUNK_SIZE = 3;
    
    @Autowired
    private final UploadFileTasklet uploadFileTasklet;

    // JobBuilderFactory를 통해서 Job을 생성
    @Bean
    public Job sampleTwoStepJob() throws Exception {
        return jobBuilderFactory.get("sampleTwoStepJob")
                .start(buildCsvStep())  // Step 1: CSV파일생성
                .next(uploadFileStep()) // Step 2: Upload파일
                .incrementer(new UniqueRunIdIncrementer()) // 중복실행허용
                .build();
    }

    // StepBuilderFactory를 통해서 Step을 생성
    @Bean
    @JobScope
    public Step buildCsvStep() throws Exception {
        return stepBuilderFactory.get("buildCsvStep")
                .<Integer, Sample>chunk(CHUNK_SIZE)
                .reader(sampleTwoStepReader())
                .processor(sampleTwoStepProcessor())
                .writer(sampleTwoStepWriter())
                .build();
    }

    // StepBuilderFactory를 통해서 Step을 생성
    @Bean
    @JobScope
    public Step uploadFileStep() throws Exception {
        return stepBuilderFactory.get("uploadFileStep")
                .tasklet(uploadFileTasklet) // Tasklet 설정
                .build();
    }

    @Bean
    public ItemReader<Integer> sampleTwoStepReader() {
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
    public ItemProcessor<Integer, Sample> sampleTwoStepProcessor() {
        return item -> {
            log.info("processing item = " + item);
            Sample sample = new Sample();
            sample.setName("sampeTwoStepName" + item);
            sample.setDescription("sampeTwoStepDescription" + item);
            return sample;
        };
    }

    @Bean
    public ItemWriter<Sample> sampleTwoStepWriter() {
        return items -> {
            log.info("About to write chunk: " + items);
            for (Sample item : items) {
                log.info("writing item = {}", item);
            }
        };
    }
    
    
}

