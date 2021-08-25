package com.x2bee.batch.app.repository.sample;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.x2bee.batch.app.entity.Sample;

@SpringBootTest
class SampleTest {
	@Autowired
	private SampleMapper sampleMapper;
	
	@Test
	void getSampleList() {
		List<Sample> result = sampleMapper.getSampleList(new Sample());
		Assertions.assertNotNull(result);
	}

}
