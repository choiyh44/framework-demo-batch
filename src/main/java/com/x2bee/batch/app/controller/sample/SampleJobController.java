/**
 * 
 */
package com.x2bee.batch.app.controller.sample;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.x2bee.batch.app.service.BatchCommonService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author choiyh44
 * @version 1.0
 * @since 2021. 9. 1.
 *
 */
@RestController
@RequestMapping("/batch/sample")
@Slf4j
public class SampleJobController {
    public static final String JOB_PARAMETERS = "jobParameters";
    
    @Autowired
    private JobRegistry jobRegistry;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private BatchCommonService batchCommonService;

    @Autowired
    private Job sampleParamJob;
    
    @GetMapping("/jobs/{jobName}")
    public String sampleJobs(@PathVariable String jobName, @RequestParam MultiValueMap<String, String> parameters) throws Exception {
        // 1) job 조회
        Job job = jobRegistry.getJob(jobName);
        // 2) 파라미터 준비 + increment(중보실행허용)
        JobParameters jobParameters = batchCommonService.setIncrementer(sampleParamJob);
        jobParameters = batchCommonService.setParameters(parameters, jobParameters);
        // 3) 실행
        JobExecution jobExecution = jobLauncher.run(sampleParamJob, jobParameters);
        log.info("Batch job has been invoked: {}", jobExecution);
        // 4) 실행성공
        return "Batch job has been invoked";
    }
  
    @GetMapping("/sampleParamJob")
    public String sampleParamJob(@RequestParam MultiValueMap<String, String> parameters) throws Exception {
        // 2) 파라미터 준비 + increment(중보실행허용)
        JobParameters jobParameters = batchCommonService.setIncrementer(sampleParamJob);
        jobParameters = new JobParametersBuilder(jobParameters).addString("sampleParam", parameters.getFirst("sampleParam")).toJobParameters();
        // 3) 실행
        JobExecution jobExecution = jobLauncher.run(sampleParamJob, jobParameters);
        log.info("Batch job has been invoked: {}", jobExecution);
        // 4) 실행성공
        return "Batch job has been invoked";
    }
  
}
