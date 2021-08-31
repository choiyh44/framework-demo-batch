/**
 * 
 */
package com.x2bee.batch.app.controller.sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobParametersNotFoundException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.support.PropertiesConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * @author choiyh44
 * @version 1.0
 * @since 2021. 8. 31.
 *
 */
@RestController
@RequestMapping("/cbatch")
@Slf4j
public class SampleComplexJobController {
    public static final String JOB_PARAMETERS = "jobParameters";

//    @Autowired
//    private JobOperator jobOperator;
    @Autowired
    private JobExplorer jobExplorer;
    @Autowired
    private JobRegistry jobRegistry;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobLauncher jobLauncher;
//    @Autowired
//    private JsrJobOperator jsrJobOperator;
    @Autowired
    private JobParametersConverter jobParametersConverter;

//    private JobLogFileNameCreator jobLogFileNameCreator = new DefaultJobLogFileNameCreator();

//    public SampleJobController(JobOperator jobOperator, JobExplorer jobExplorer, JobRegistry jobRegistry,
//            JobRepository jobRepository, JobLauncher jobLauncher, JsrJobOperator jsrJobOperator) {
//        super();
//        this.jobOperator = jobOperator;
//        this.jobExplorer = jobExplorer;
//        this.jobRegistry = jobRegistry;
//        this.jobRepository = jobRepository;
//        this.jobLauncher = jobLauncher;
//        this.jsrJobOperator = jsrJobOperator;
//    }

    @PostMapping(value = "/jobs/{jobName}")
    public String launch(@PathVariable String jobName, @RequestParam MultiValueMap<String, String> payload)
            throws NoSuchJobException, JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException, JobParametersNotFoundException {
        String parameters = payload.getFirst(JOB_PARAMETERS);
        if (log.isDebugEnabled()) {
            log.debug("Attempt to start job with name {} and parameters {}", jobName, parameters);
        }
        try {
            Job job = jobRegistry.getJob(jobName);
            JobParameters jobParameters = createJobParametersWithIncrementerIfAvailable(parameters, job);
            Long id = jobLauncher.run(job, jobParameters).getId();
            return String.valueOf(id);
        } catch (NoSuchJobException e) {
            throw e;
        }
    }
    private JobParameters createJobParametersWithIncrementerIfAvailable(String parameters, Job job)
            throws JobParametersNotFoundException {
        JobParameters jobParameters = jobParametersConverter
                .getJobParameters(PropertiesConverter.stringToProperties(parameters));
        // use JobParametersIncrementer to create JobParameters if incrementer is set and only if the job is no restart
        if (job.getJobParametersIncrementer() != null) {
            JobExecution lastJobExecution = jobRepository.getLastJobExecution(job.getName(), jobParameters);
            boolean restart = false;
            // check if job failed before
            if (lastJobExecution != null) {
                BatchStatus status = lastJobExecution.getStatus();
                if (status.isUnsuccessful() && status != BatchStatus.ABANDONED) {
                    restart = true;
                }
            }
            // if it's not a restart, create new JobParameters with the incrementer
            if (!restart) {
                JobParameters nextParameters = getNextJobParameters(job);
                Map<String, JobParameter> map = new HashMap<String, JobParameter>(nextParameters.getParameters());
                map.putAll(jobParameters.getParameters());
                jobParameters = new JobParameters(map);
            }
        }
        return jobParameters;
    }

    /**
     * Borrowed from CommandLineJobRunner.
     *
     * @param job
     *            the job that we need to find the next parameters for
     * @return the next job parameters if they can be located
     * @throws JobParametersNotFoundException
     *             if there is a problem
     */
    private JobParameters getNextJobParameters(Job job) throws JobParametersNotFoundException {
        String jobIdentifier = job.getName();
        JobParameters jobParameters;
        List<JobInstance> lastInstances = jobExplorer.getJobInstances(jobIdentifier, 0, 1);

        JobParametersIncrementer incrementer = job.getJobParametersIncrementer();

        if (lastInstances.isEmpty()) {
            jobParameters = incrementer.getNext(new JobParameters());
            if (jobParameters == null) {
                throw new JobParametersNotFoundException(
                        "No bootstrap parameters found from incrementer for job=" + jobIdentifier);
            }
        } else {
            List<JobExecution> lastExecutions = jobExplorer.getJobExecutions(lastInstances.get(0));
            jobParameters = incrementer.getNext(lastExecutions.get(0).getJobParameters());
        }
        return jobParameters;
    }

}
