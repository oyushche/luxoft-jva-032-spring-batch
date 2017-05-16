package com.luxoft.errors;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Iterator;

//@Configuration
//@EnableBatchProcessing
public class SkipConf
{

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    public ItemReader<String> reader()
    {
        return new ItemReader<String>()
        {
            private Iterator<String> it = Arrays.asList("one", "two", "three", "four", "five", "six").iterator();

            @Override
            public String read() throws Exception
            {
                if (it.hasNext())
                {
                    String s = it.next();
                    if ("two".equals(s)) {
                        System.out.println("=======>>>>>> should skip read for: " + s);
                        throw new IllegalStateException();
                    }

                    return s;
                }
                return null;
            }
        };
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

    public ItemProcessor<String, String> processor()
    {
        return new ItemProcessor<String, String>()
        {
            private boolean retry = false;
            private int attempt = 0;

            @Override
            public String process(String item) throws Exception
            {
                if ("five".equals(item)) {
                    System.out.println("=======>>>>>> should skip: " + item);
                    throw new IllegalStateException();
                }

                return item.toUpperCase();
            }
        };
    }

    @Bean
    public Step step()
    {
        return stepBuilderFactory.get("step A")
                .<String, String>chunk(2)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .faultTolerant()
                .skip(IllegalStateException.class)
                .skipLimit(5)
                .build();
    }

    @Bean
    public Job jobForListeners()
    {
        return jobBuilderFactory.get("jobForListeners").start(step()).build();
    }
}
