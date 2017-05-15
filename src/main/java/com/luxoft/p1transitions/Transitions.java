package com.luxoft.p1transitions;

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
public class Transitions
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
            System.out.println("==================>>>>>>> step 1");
            return RepeatStatus.FINISHED;
        }).build();
    }

    @Bean
    public Step step2()
    {
        return stepBuilderFactory.get("step2").tasklet((stepContribution, chunkContext) ->
        {
            System.out.println("==================>>>>>>> step 2");
            return RepeatStatus.FINISHED;
        }).build();
    }

    @Bean
    public Step step3()
    {
        return stepBuilderFactory.get("step3").tasklet((stepContribution, chunkContext) ->
        {
            System.out.println("==================>>>>>>> step 3");
            return RepeatStatus.FINISHED;
        }).build();
    }

    @Bean
    public Job hiJob()
    {
        return jobBuilderFactory.get("p1transitions")
                // first
                .start(step1())
                .next(step2())
                .next(step3())
                .next(step2())


                // second
//                .start(step1())
//                .on("COMPLETED").to(step2())
//                .from(step2()).on("COMPLETED").to(step3())
//                .from(step3()).end()

                // third
//                .start(step1())
//                .on("COMPLETED").to(step2())
//                .from(step2()).on("COMPLETED").fail()
//                .from(step3()).end()

                // forth
//                .start(step1())
//                .on("COMPLETED").to(step2())
//                .from(step2()).on("COMPLETED").stop()
//                .end()

                // then
                .build();
    }
}
