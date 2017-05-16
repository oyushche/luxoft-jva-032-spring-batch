package com.luxoft.scaling;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.integration.partition.BeanFactoryStepLocator;
import org.springframework.batch.integration.partition.MessageChannelPartitionHandler;
import org.springframework.batch.integration.partition.StepExecutionRequestHandler;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

// TODO not finished
//@Configuration
//@EnableBatchProcessing
public class RemotePartitioningConf implements ApplicationContextAware
{
    public static final int COUNT = 1_00_000;
    public static final int CHUNK = 2_000;
    public static Integer[] data;
    public static final AtomicInteger COUNT_OF_WROTE = new AtomicInteger();

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobExplorer jobExplorer;

    private ApplicationContext applicationContext;

    @Bean
    @StepScope
    public ItemReader<Integer> reader(@Value("#{stepExecutionContext['minValue']}") Integer minValue, @Value("#{stepExecutionContext['maxValue']}") Integer maxValue)
    {
        generateData();

        Integer[] numbers = Arrays.copyOfRange(data, minValue, maxValue);

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

    @Bean
    public PartitionHandler partitionHandler(MessagingTemplate template) throws Exception
    {
        MessageChannelPartitionHandler handler = new MessageChannelPartitionHandler();

        handler.setStepName("slaveStep");
        handler.setGridSize(4);
        handler.setMessagingOperations(template);
        handler.setPollInterval(5000);
        handler.setJobExplorer(jobExplorer);

        handler.afterPropertiesSet();

        return handler;
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
    @Profile("slave")
    @ServiceActivator(inputChannel = "in", outputChannel = "out")
    public StepExecutionRequestHandler stepExecutionRequestHandler()
    {
        StepExecutionRequestHandler handler = new StepExecutionRequestHandler();

        BeanFactoryStepLocator locator = new BeanFactoryStepLocator();
        locator.setBeanFactory(applicationContext);

        handler.setStepLocator(locator);
        handler.setJobExplorer(jobExplorer);

        return handler;
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata defaultPoller()
    {
        PollerMetadata metadata = new PollerMetadata();

        metadata.setTrigger(new PeriodicTrigger(10));

        return metadata;
    }

    @Bean
    public Step step()
    {
        return stepBuilderFactory.get("step A")
                .partitioner(slaveStep().getName(), partitioner())
                .step(slaveStep())
                .gridSize(4)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }
}
