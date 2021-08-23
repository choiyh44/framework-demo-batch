package com.x2bee.batch.app.repository.sample;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.x2bee.batch.app.sample.entity.Sample;

@Repository
public interface SampleMapper {
	public List<Sample> getSampleList(Sample sample);
}
