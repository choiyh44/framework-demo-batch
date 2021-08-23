package com.x2bee.batch;

import javax.annotation.PostConstruct;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableBatchProcessing
@Slf4j
@MapperScan(basePackages = "com.x2bee.batch.app.repository")
public class FrameworkDemoBatchApplication {
    @Value("${spring.batch.job.names:NONE}")
    private String jobNames;
    

	public static void main(String[] args) {
		SpringApplication.run(FrameworkDemoBatchApplication.class, args);
	}

    // spring.batch.job.names 를 지정하지 않으면 모든 Job이 실행돼 버리기 때문에
    // 방어차원에서 넣은 job.names validation 처리    
    @PostConstruct
    public void validateJobNames() {
        log.info("jobNames : {}", jobNames);
        if (jobNames.isEmpty() || jobNames.equals("NONE")) {
            throw new IllegalStateException("spring.batch.job.names=job1,job2 형태로 실행을 원하는 Job을 명시해야만 합니다!");
        }
    }
    
}
