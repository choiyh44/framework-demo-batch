/**
 * 
 */
package com.x2bee.batch.app.jobconfig.sample;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author choiyh44
 * @version 1.0
 * @since 2021. 8. 30.
 *
 */
@Component
@Slf4j
public class UploadFileTasklet implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        
        log.info("!!!!!! Step Two UploadFileTasklet executed !!!!!!: ");
       
        return RepeatStatus.FINISHED;
    }
}
