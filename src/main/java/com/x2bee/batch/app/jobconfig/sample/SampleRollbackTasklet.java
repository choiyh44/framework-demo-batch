package com.x2bee.batch.app.jobconfig.sample;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.x2bee.batch.app.service.sample.SampleService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SampleRollbackTasklet implements Tasklet {
	@Autowired
	private SampleService sampleService;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		sampleService.testRollback();
		
	    return RepeatStatus.FINISHED;
	}
}
