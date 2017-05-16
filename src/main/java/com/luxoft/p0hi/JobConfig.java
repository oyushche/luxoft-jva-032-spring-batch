package com.luxoft.p0hi;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//@EnableBatchProcessing
public class JobConfig
{
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step step1()
    {
        return stepBuilderFactory.get("step1").tasklet((stepContribution, chunkContext) ->
        {
            System.out.println("==================>>>>>>> step 1 tasklet");
            return RepeatStatus.FINISHED;
        }).build();
    }

    @Bean
    public Job hiJob()
    {
        return jobBuilderFactory.get("hiJob").start(step1()).build();
    }
}
