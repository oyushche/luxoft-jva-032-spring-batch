package com.luxoft.p6listeners;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

//@Configuration
//@EnableBatchProcessing
public class ListenersJobConf
{

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public ItemReader<String> reader()
    {
        return new ListItemReader<>(Arrays.asList("one", "two", "three"));
    }

    @Bean
    public ItemWriter<String> writer()
    {
        return list ->
        {
            for (String s : list)
            {
                System.out.println("===>>>> writing: " + s);
            }
        };
    }

    @Bean
    public Step step()
    {
        return stepBuilderFactory.get("step A")
                .<String, String>chunk(2)
                .faultTolerant()
                .listener(new ChunkListener())
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean
    public Job jobForListeners()
    {
        return jobBuilderFactory.get("jobForListeners")
                .start(step())
                .listener(new JobListener())
                .build();
    }
}
