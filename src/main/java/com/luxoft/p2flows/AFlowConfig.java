package com.luxoft.p2flows;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

//@Configuration
//@EnableBatchProcessing
public class AFlowConfig
{

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step stepA()
    {
        return stepBuilderFactory.get("stepA").tasklet((stepContribution, chunkContext) ->
        {
            System.out.println("==================>>>>>>> step A");
            return RepeatStatus.FINISHED;
        }).build();
    }

    @Bean
    public Job aFlowJob(Flow flow)
    {
//        return jobBuilderFactory.get("aFlowJob").start(flow).next(stepA()).end().build();
        return jobBuilderFactory.get("aFlowJob")
                .start(stepA())
                .on("COMPLETED").to(flow)
                .end().build();
    }
}
