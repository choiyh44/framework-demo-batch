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
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class SampleStepFlowConfig {
    private final JobBuilderFactory jobBuilderFactory; // Job 빌더 생성용
    private final StepBuilderFactory stepBuilderFactory; // Step 빌더 생성용
    
    private static final int CHUNK_SIZE = 3;
    
    @Autowired
    private final UploadFileTasklet uploadFileTasklet;

    // JobBuilderFactory를 통해서 Job을 생성
    @Bean
    public Job sampleStepFlowJob() throws Exception {
        return jobBuilderFactory.get("sampleStepFlowJob")
                .start(buildCsvStep2()).on("FAILED").end()  // Step 1: CSV파일생성
                .on("*").to(uploadFileStep2()).end() // Step 2: Upload파일
                .incrementer(new UniqueRunIdIncrementer()) // 중복실행허용
                .build();
    }

    // StepBuilderFactory를 통해서 Step을 생성
    @Bean
    @JobScope
    public Step buildCsvStep2() throws Exception {
        return stepBuilderFactory.get("buildCsvStep2")
                .<Integer, Sample>chunk(CHUNK_SIZE)
                .reader(sampleStepFlowReader())
                .processor(sampleStepFlowProcessor(null))
                .writer(sampleStepFlowWriter())
                .build();
    }

    // StepBuilderFactory를 통해서 Step을 생성
    @Bean
    @JobScope
    public Step uploadFileStep2() throws Exception {
        return stepBuilderFactory.get("uploadFileStep2")
                .tasklet(uploadFileTasklet) // Tasklet 설정
                .build();
    }

    @Bean
    public ItemReader<Integer> sampleStepFlowReader() {
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
    @StepScope
    public ItemProcessor<Integer, Sample> sampleStepFlowProcessor(@Value("#{jobParameters['dummy']}") String dummy) {
        return item -> {
            if ("x".equalsIgnoreCase(dummy) && item == 3) {
                throw new IllegalArgumentException("Unable to process 5");
            }
            log.info("processing item = " + item);
            Sample sample = new Sample();
            sample.setName("sampeStepFlowName" + item);
            sample.setDescription("sampeStepFlowDescription" + item);
            return sample;
        };
    }

    @Bean
    public ItemWriter<Sample> sampleStepFlowWriter() {
        return items -> {
            log.info("About to write chunk: " + items);
            for (Sample item : items) {
                log.info("writing item = {}", item);
            }
        };
    }
    
    
}

