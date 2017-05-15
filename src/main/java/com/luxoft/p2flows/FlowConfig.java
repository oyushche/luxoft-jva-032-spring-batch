package com.luxoft.p2flows;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

//@Configuration
//@EnableBatchProcessing
public class FlowConfig
{
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step step1()
    {
        return stepBuilderFactory.get("step1").tasklet((stepContribution, chunkContext) ->
        {
            System.out.println("==================>>>>>>> myFstFlow: step 1");
            return RepeatStatus.FINISHED;
        }).build();
    }

    @Bean
    public Step step2()
    {
        return stepBuilderFactory.get("step2").tasklet((stepContribution, chunkContext) ->
        {
            System.out.println("==================>>>>>>> myFstFlow: step 2");
            return RepeatStatus.FINISHED;
        }).build();
    }

    @Bean
    public Step step3()
    {
        return stepBuilderFactory.get("step3").tasklet((stepContribution, chunkContext) ->
        {
            System.out.println("==================>>>>>>> myFstFlow: step 3");
            return RepeatStatus.FINISHED;
        }).build();
    }

    @Bean
    public Flow flow()
    {
        FlowBuilder<Flow> builder = new FlowBuilder<>("myFstFlow");

        builder.start(step1()).next(step2()).end();

        return builder.build();
    }
}
