package com.luxoft.output;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.*;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//@Configuration
//@EnableBatchProcessing
public class WriterConf
{

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    public ItemReader<String> reader()
    {
        return new ItemReader<String>()
        {
            private Iterator<String> it = Arrays.asList("one", "two", "three").iterator();

            @Override
            public String read() throws Exception
            {
                if (it.hasNext())
                {
                    return it.next();
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

    public JdbcBatchItemWriter<String> jdbcBatchItemWriter()
    {
        JdbcBatchItemWriter<String> writer = new JdbcBatchItemWriter<>();

        writer.setDataSource(null);
        writer.setSql("");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.afterPropertiesSet();

        return writer;
    }

    @Bean
    public Step step()
    {
        return stepBuilderFactory.get("step A")
                .<String, String>chunk(2)
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean
    public Job jobForListeners()
    {
        return jobBuilderFactory.get("jobForListeners")
                .start(step())
                .build();
    }
}
