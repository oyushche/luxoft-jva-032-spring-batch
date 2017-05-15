package com.luxoft.scaling;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

//@Configuration
//@EnableBatchProcessing
public class LocalPartitioningConf
{
    public static final int COUNT = 1_00_000;
    public static final int CHUNK = 2_000;
    public static Integer[] data;
    public static final AtomicInteger COUNT_OF_WROTE = new AtomicInteger();

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public ItemReader<Integer> reader(@Value("#{stepExecutionContext['minValue']}") Integer minValue, @Value("#{stepExecutionContext['maxValue']}") Integer maxValue)
    {
        generateData();

        Integer[] numbers = Arrays.copyOfRange(data, minValue, maxValue);;

        return new ListItemReader<>(Arrays.asList(numbers));
    }

    private void generateData()
    {
        if (data == null)
        {
            Random rnd = new Random();
            Integer[] numbers = new Integer[COUNT];

            for (int i = 0; i < COUNT; i++)
            {
                numbers[i] = rnd.nextInt(COUNT);
            }

            data = numbers;
        }
    }

    public ItemWriter<String> writer()
    {
        return list -> System.out.println("======>>>> COMPLETE: " + ((COUNT_OF_WROTE.addAndGet(list.size()) * 100 / COUNT)) + "%");
    }

    public ItemProcessor<Integer, String> processor()
    {
        return i -> i.toString();
    }

    public Partitioner partitioner()
    {
        return gridSize ->
        {
            Map<String, ExecutionContext> map = new HashMap<>();

            for (int i = 0; i < COUNT; i += CHUNK)
            {
                ExecutionContext context = new ExecutionContext();

                map.put("partition" + i, context);

                context.putInt("minValue", i);
                context.putInt("maxValue", i + CHUNK);
//                System.out.println("======>>>> partition created from: " + i + " to: " + (i + CHUNK));
            }

            return map;
        };
    }

    @Bean
    public Step step()
    {
        return stepBuilderFactory.get("step A").partitioner(slaveStep().getName(), partitioner()).step(slaveStep()).gridSize(4).taskExecutor(new SimpleAsyncTaskExecutor()).build();
    }

    @Bean
    public Step slaveStep()
    {
        return stepBuilderFactory.get("slaveStep").<Integer, String>chunk(CHUNK)
                .reader(reader(null, null))
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job jobPartitioning()
    {
        return jobBuilderFactory.get("jobPartitioning").start(step()).build();
    }
}
