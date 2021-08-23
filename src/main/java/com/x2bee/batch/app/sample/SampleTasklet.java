package com.x2bee.batch.app.sample;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.x2bee.batch.app.repository.sample.SampleMapper;
import com.x2bee.batch.app.sample.entity.Sample;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SampleTasklet implements Tasklet {
	@Autowired
	private SampleMapper sampleMapper;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		List<Sample> list = sampleMapper.getSampleList(new Sample());
		for (Sample sample : list) {
			log.info("!!!!!! executed tasklet !!!!!!: {}", sample);
		}
		
	    return RepeatStatus.FINISHED;
	}

}
