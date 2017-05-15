package com.luxoft.messaging;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.integration.launch.JobLaunchingMessageHandler;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;

//@Configuration
public class MessageLaunchConfig
{

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    JobLauncher jobLauncher;

    @Bean
    @ServiceActivator(inputChannel = "requests", outputChannel = "replies")
    protected JobLaunchingMessageHandler jobLaunchingMessageHandler()
    {
        return new JobLaunchingMessageHandler(jobLauncher);
    }

    @Bean
    public DirectChannel requests()
    {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel replies()
    {
        return new DirectChannel();
    }

    @Bean
    public Step step()
    {
        return stepBuilderFactory.get("step").tasklet((stepContribution, chunkContext) ->
        {
            System.out.println("==================>>>>>>> actual run");
            return RepeatStatus.FINISHED;
        }).build();
    }

    @Bean
    public Job messageJob()
    {
        return jobBuilderFactory.get("messageJob").start(step()).build();
    }
}
