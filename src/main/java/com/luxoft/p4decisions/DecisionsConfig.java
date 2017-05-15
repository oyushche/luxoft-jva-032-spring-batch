package com.luxoft.p4decisions;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.Random;

//@Configuration
//@EnableBatchProcessing
public class DecisionsConfig
{

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    public Tasklet tasklet(String name)
    {
        return (contribution, chunkContext) ->
        {
            System.out.println("=========== >>>>>>>> " + name);

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step first()
    {
        return stepBuilderFactory.get("first").tasklet(tasklet("first")).build();
    }

    @Bean
    public Step even()
    {
        return stepBuilderFactory.get("even").tasklet(tasklet("even")).build();
    }

    @Bean
    public Step odd()
    {
        return stepBuilderFactory.get("odd").tasklet(tasklet("odd")).build();
    }

    @Bean
    public JobExecutionDecider decider()
    {
        return (jobExecution, stepExecution) ->
        {
            Random r = new Random();

            if (r.nextInt(2) % 2 == 0) {
                return new FlowExecutionStatus("EVEN");
            }
            return new FlowExecutionStatus("ODD");
        };
    }

    @Bean
    public Job job()
    {
        return jobBuilderFactory.get("decideJob")
                .start(first())
                .next(decider())
                .from(decider()).on("EVEN").to(even())
                .from(decider()).on("ODD").to(odd())
                .from(odd()).on("*").to(decider())
                .end().build();
    }


}
