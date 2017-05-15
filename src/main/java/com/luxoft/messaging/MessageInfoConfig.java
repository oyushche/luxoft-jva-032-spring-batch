package com.luxoft.messaging;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.gateway.GatewayProxyFactoryBean;
import org.springframework.integration.stream.CharacterStreamWritingMessageHandler;

import java.util.ArrayList;
import java.util.List;

//@Configuration
public class MessageInfoConfig implements ApplicationContextAware
{
    public static final int COUNT = 100;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    private ApplicationContext applicationContext;

    @Bean
    public ListItemReader<String> reader()
    {
        List<String> items = new ArrayList<>(COUNT);

        for (int i = 0; i < COUNT; i++)
        {
            items.add(String.valueOf(i));
        }

        return new ListItemReader<>(items);
    }

    @Bean
    public ItemWriter<String> writer()
    {
        return items ->
        {
            for (String item : items)
            {
                System.out.println("================>>>>>>>>>>>> " + item);
            }
        };
    }

    @Bean
    public Step step()
    {
        return stepBuilderFactory.get("step")
                .<String, String>chunk(COUNT / 20)
                .reader(reader())
                .writer(writer())
                .listener((ChunkListener) chunkListener())
                .build();
    }

    @Bean
    public Job job()
    {
        return jobBuilderFactory.get("job")
                .start(step())
                .listener((JobExecutionListener) jobExecListener())
                .build();
    }

    @Bean
    public Object jobExecListener()
    {
        GatewayProxyFactoryBean proxy = new GatewayProxyFactoryBean(JobExecutionListener.class);

        proxy.setDefaultRequestChannel(events());
        proxy.setBeanFactory(applicationContext);

        proxy.afterPropertiesSet();

        return proxy;
    }

    @Bean
    public Object chunkListener()
    {
        GatewayProxyFactoryBean proxy = new GatewayProxyFactoryBean(ChunkListener.class);

        proxy.setDefaultRequestChannel(events());
        proxy.setBeanFactory(applicationContext);

        proxy.afterPropertiesSet();

        return proxy;
    }

    @Bean
    public DirectChannel events()
    {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "events")
    public CharacterStreamWritingMessageHandler logger()
    {
        return CharacterStreamWritingMessageHandler.stderr();
    }

    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }
}
