package com.luxoft.errors;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

//@Configuration
//@EnableBatchProcessing
public class RestartConf
{

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public Tasklet restart()
    {
        return (stepContribution, chunkContext) ->
        {
            Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();

            if (stepExecutionContext.containsKey("run"))
            {
                System.out.println("Green light.");
                return RepeatStatus.FINISHED;
            }
            else
            {
                System.out.println("RED LIGHT");
                chunkContext.getStepContext().getStepExecution().getExecutionContext().put("run", true);
                throw new RuntimeException("Step execution prevented.");
            }
        };
    }


    @Bean
    public Step step1()
    {
        return stepBuilderFactory.get("step 1")
                .tasklet(restart())
                .build();
    }

    @Bean
    public Step step2()
    {
        return stepBuilderFactory.get("step 2")
                .tasklet(restart())
                .build();
    }

    @Bean
    public Job jobForListeners()
    {
        return jobBuilderFactory
                .get("jobForListeners")
                .start(step1())
                .next(step2())
                .build();
    }
}
