/**
 * 
 */
package com.x2bee.batch.app.jobconfig.sample;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.x2bee.batch.app.entity.Sample;
import com.x2bee.batch.base.UniqueRunIdIncrementer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author choiyh44
 * @version 1.0
 * @since 2021. 8. 30.
 *
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SampleCsvReaderConfig {
    private final JobBuilderFactory jobBuilderFactory; // Job 빌더 생성용
    private final StepBuilderFactory stepBuilderFactory; // Step 빌더 생성용
    
    private static final int CHUNK_SIZE = 3;
    
    // JobBuilderFactory를 통해서 Job을 생성
    @Bean
    public Job sampleCsvReaderJob() throws Exception {
        return jobBuilderFactory.get("sampleCsvReaderJob")
                .start(sampleCsvReaderStep())  // Step 설정
                .incrementer(new UniqueRunIdIncrementer()) // 중복실행허용
                .build();
    }

    // StepBuilderFactory를 통해서 Step을 생성
    @Bean
    public Step sampleCsvReaderStep() throws Exception {
        return stepBuilderFactory.get("sampleCsvReaderStep")
                .<Sample, String>chunk(CHUNK_SIZE)
                .reader(sampleCsvReaderReader())
                .processor(sampleCsvReaderProcessor())
                .writer(sampleCsvReaderWriter())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(3)
                .build();
    }

    @Bean
    public ItemReader<Sample> sampleCsvReaderReader() {
        FlatFileItemReader<Sample> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("/csv/sample_data.csv"));
        //flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(new DefaultLineMapper<Sample>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames("name", "description");
                    }
                });

                setFieldSetMapper(new BeanWrapperFieldSetMapper<Sample>(){
                    {
                        setTargetType(Sample.class);
                    }
                });
            }
        });
        return flatFileItemReader;
    }
    
    @Bean
    public ItemProcessor<Sample, String> sampleCsvReaderProcessor() {
        return item -> {
            String str = item.toString();
            log.info("processing item = " + str);
            return str;
        };
    }

    @Bean
    public ItemWriter<String> sampleCsvReaderWriter() {
        return items -> {
            log.info("About to write chunk: " + items);
            for (String item : items) {
                log.info("writing item = " + item);
            }
        };
    }
    
}

