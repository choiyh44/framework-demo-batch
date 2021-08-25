package com.x2bee.batch.app.jobconfig.sample;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.x2bee.batch.app.entity.Sample;
import com.x2bee.batch.app.service.sample.SampleService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SampleTasklet implements Tasklet {
	@Autowired
	private SampleService sampleService;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		List<Sample> list = sampleService.getSampleList(new Sample());
		for (Sample sample : list) {
			log.info("!!!!!! executed tasklet !!!!!!: {}", sample);
		}
		
	    return RepeatStatus.FINISHED;
	}

}
