/**
 * 
 */
package com.x2bee.batch.base.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author choiyh44
 * @version 1.0
 * @since 2021. 9. 1.
 *
 */
@Configuration
public class BatchConfig {
    @Autowired
    private JobRegistry jobRegistry;
    
    @Bean
    @ConditionalOnMissingBean
    public JobParametersConverter jobParametersConverter() {
        return new DefaultJobParametersConverter();
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        return postProcessor;
    }
}
