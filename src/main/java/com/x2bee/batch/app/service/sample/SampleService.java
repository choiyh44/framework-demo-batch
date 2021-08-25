package com.x2bee.batch.app.service.sample;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.x2bee.batch.app.entity.Sample;
import com.x2bee.batch.app.repository.sample.SampleMapper;
import com.x2bee.batch.app.repository.sample.SampleTrxMapper;

@Service
public class SampleService {
	@Autowired
	private SampleMapper sampleMapper;
	
	@Autowired
	private SampleTrxMapper sampleTrxMapper;

	public List<Sample> getSampleList(Sample sample) {
		return sampleMapper.getSampleList(sample);
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = false)
	public void insertSample() {
		Sample sample;
		
		sample = new Sample();
		sample.setName("testName1");
		sample.setDescription("testDescription1");
		sample.setSysRegrId("BATCH");
		sample.setSysModrId("BATCH");
		sampleTrxMapper.insertSample(sample);
		
		sample = new Sample();
		sample.setName("testName2");
		sample.setDescription("testDescription2");
		sample.setSysRegrId("BATCH");
		sample.setSysModrId("BATCH");
		sampleTrxMapper.insertSample(sample);
				
	}

	public void testRollback() throws Exception {
		Sample sample;
		
		sample = new Sample();
		sample.setName("testName3");
		sample.setDescription("testDescription3");
		sample.setSysRegrId("BATCH");
		sample.setSysModrId("BATCH");
		sampleTrxMapper.insertSample(sample);
		
		if (true) {
			throw new Exception("롤백 테스트 Exception 발생!!!");
		}
		sample = new Sample();
		sample.setName("testName4");
		sample.setDescription("testDescription4");
		sample.setSysRegrId("BATCH");
		sample.setSysModrId("BATCH");
		sampleTrxMapper.insertSample(sample);
	}
}
