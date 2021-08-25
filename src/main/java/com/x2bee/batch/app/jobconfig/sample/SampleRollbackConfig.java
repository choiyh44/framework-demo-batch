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
public class SampleRollbackConfig {
	private final JobBuilderFactory jobBuilderFactory; // Job 빌더 생성용
    private final StepBuilderFactory stepBuilderFactory; // Step 빌더 생성용

    @Autowired
    private SampleRollbackTasklet sampleRollbackTasklet;
    
    // JobBuilderFactory를 통해서 tutorialJob을 생성
    @Bean
    public Job sampleRollbackJob() {
        return jobBuilderFactory.get("sampleRollbackJob")
                .start(sampleRollbackStep())  // Step 설정
                .incrementer(new UniqueRunIdIncrementer())
                .build();
    }

    // StepBuilderFactory를 통해서 tutorialStep을 생성
    @Bean
    @JobScope
    public Step sampleRollbackStep() {
        return stepBuilderFactory.get("sampleRollbackStep")
                .tasklet(sampleRollbackTasklet) // Tasklet 설정
                .build();
    }

}
