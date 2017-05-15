package com.luxoft.scaling;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import java.util.Arrays;
import java.util.Random;

//@Configuration
//@EnableBatchProcessing
public class MultipleThreadsConf
{

    public static final int COUNT = 1_00_000;
    public static final int CHUNK = 1_000;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    public ItemReader<Integer> reader()
    {
        Random rnd = new Random();
        Integer[] data = new Integer[COUNT];

        for (int i = 0; i < COUNT; i++)
        {
            data[i] = rnd.nextInt(COUNT);
        }

        return new ListItemReader<>(Arrays.asList(data));
    }

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

    public ItemProcessor<Integer, String> processor()
    {
        return i -> i.toString();
    }

    @Bean
    public Step step()
    {
        return stepBuilderFactory.get("step A")
                .<Integer, String>chunk(CHUNK)
                .reader(reader())
                .processor(processor())
//                .writer(writer())
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public Job jobForListeners()
    {
        return jobBuilderFactory.get("jobForListeners").start(step()).build();
    }
}
