package com.x2bee.batch.app.jobconfig.sample;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.x2bee.batch.base.UniqueRunIdIncrementer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SampleSelectConfig {
	private final JobBuilderFactory jobBuilderFactory; // Job 빌더 생성용
    private final StepBuilderFactory stepBuilderFactory; // Step 빌더 생성용

    @Autowired
    private SampleSelectTasklet sampleTasklet;
    
    // JobBuilderFactory를 통해서 Job을 생성
    @Bean
    public Job sampleSelectJob() {
        return jobBuilderFactory.get("sampleSelectJob")
                .start(sampleSelectStep())  // Step 설정
                .incrementer(new UniqueRunIdIncrementer())
                .build();
    }

    // StepBuilderFactory를 통해서 Step을 생성
    @Bean
    @JobScope
    public Step sampleSelectStep() {
        return stepBuilderFactory.get("sampleSelectStep")
                .tasklet(sampleTasklet) // Tasklet 설정
                .build();
    }
    
}
