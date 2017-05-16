package com.luxoft.p3split;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

//@Configuration
//@EnableBatchProcessing
public class SplitConfig
{

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Tasklet tasklet()
    {
        return (contribution, chunkContext) ->
        {
            System.out.println(String.format("%s executed on thread %s",
                    chunkContext.getStepContext().getStepName(),
                    Thread.currentThread().getName()));

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Flow flow1()
    {
        return new FlowBuilder<Flow>("F1")
                .start(stepBuilderFactory
                        .get("step1")
                        .tasklet(tasklet()).build())
                .build();
    }

    @Bean
    public Flow flow2()
    {
        return new FlowBuilder<Flow>("F2")
                .start(stepBuilderFactory.get("step2")
                        .tasklet(tasklet()).build())
                .next(stepBuilderFactory.get("step3")
                        .tasklet(tasklet()).build()).build();
    }

    @Bean
    public Job job()
    {
        return jobBuilderFactory.get("splitJob")
                .start(flow1())
                .split(new SimpleAsyncTaskExecutor())
                .add(flow2())
                .end()
                .build();
    }
}
