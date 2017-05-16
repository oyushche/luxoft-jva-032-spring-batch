package com.luxoft.p5nested;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.JobStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

//@Configuration
//@EnableBatchProcessing
public class ParentJob
{

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private Job firstChildJob;

    @Autowired
    private JobLauncher launcher;

    @Bean
    public Step parent()
    {
        return stepBuilderFactory.get("parent").tasklet((stepContribution, chunkContext) ->
        {
            System.out.println("==================>>>>>>> parent");
            return RepeatStatus.FINISHED;
        }).build();
    }

    @Bean
    public Job firstParentJob(JobRepository repository, PlatformTransactionManager txManager)
    {
        Step child = new JobStepBuilder(new StepBuilder("childStep"))
                .job(firstChildJob)
                .launcher(launcher)
                .repository(repository)
                .transactionManager(txManager)
                .build();

        return jobBuilderFactory.get("parent").start(parent()).next(child).build();
    }
}
