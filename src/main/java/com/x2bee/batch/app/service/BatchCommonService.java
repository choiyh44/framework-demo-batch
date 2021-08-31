/**
 * 
 */
package com.x2bee.batch.app.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobParametersNotFoundException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author choiyh44
 * @version 1.0
 * @since 2021. 9. 2.
 *
 */
@Component
public class BatchCommonService {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobExplorer jobExplorer;

    /**
     * 파라미터 설정
     * @param parameters
     * @return
     * @throws JobParametersNotFoundException
     */
    public JobParameters setParameters(@RequestParam MultiValueMap<String, String> parameters) throws JobParametersNotFoundException {
        return setParameters(parameters, null);
    }
    /**
     * 파라미터 설정
     * @param parameters
     * @param jobParameters
     * @return
     * @throws JobParametersNotFoundException
     */
    public JobParameters setParameters(@RequestParam MultiValueMap<String, String> parameters, JobParameters jobParameters) throws JobParametersNotFoundException {
        if (jobParameters == null) {
            jobParameters = new JobParameters();
        }
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder(jobParameters);
        parameters.entrySet().stream().forEach(entry -> jobParametersBuilder.addString(entry.getKey(), entry.getValue().get(0)));
        return jobParametersBuilder.toJobParameters();
    }
    /**
     * incrementer 설정. 중복허용 시 사용.
     * @param job
     * @return
     * @throws JobParametersNotFoundException
     */
    public JobParameters setIncrementer(Job job) throws JobParametersNotFoundException {
        return addIncrementer(job, null);
    }

    /**
     * incrementer 설정. 중복허용 시 사용.
     * @param job
     * @param jobParameters
     * @return
     * @throws JobParametersNotFoundException
     */
    public JobParameters addIncrementer(Job job, JobParameters jobParameters) throws JobParametersNotFoundException {
        if (jobParameters == null) {
            jobParameters = new JobParameters();
        }
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
