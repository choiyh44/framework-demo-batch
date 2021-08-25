package com.x2bee.batch.app.repository.sample;

import org.springframework.stereotype.Repository;

import com.x2bee.batch.app.entity.Sample;

@Repository
public interface SampleTrxMapper {
	public void insertSample(Sample sample);
}
