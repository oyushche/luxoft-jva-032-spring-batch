package com.luxoft.p7params;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

//@Configuration
//@EnableBatchProcessing
public class ParamsConfig
{

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public Tasklet messageTasklet(@Value("#{jobParameters['message']}") String message)
    {
        return (stepContribution, chunkContext) ->
        {
            System.out.println("==================>>>>>>> " + message);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step step1()
    {
        return stepBuilderFactory.get("step1").tasklet(messageTasklet(null)).build();
    }

    @Bean
    public Job hiJob()
    {
        return jobBuilderFactory.get("hiJob").start(step1()).build();
    }
}
