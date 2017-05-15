package com.luxoft.p5nested;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

//@Configuration
//@EnableBatchProcessing
public class ChildJob
{

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step child()
    {
        return stepBuilderFactory.get("child").tasklet((stepContribution, chunkContext) ->
        {
            System.out.println("==================>>>>>>> child");
            return RepeatStatus.FINISHED;
        }).build();
    }

    @Bean
    public Job firstChildJob()
    {
        return jobBuilderFactory.get("childJob").start(child()).build();
    }
}
