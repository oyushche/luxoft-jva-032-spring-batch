package com.luxoft.input;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.*;

//@Configuration
//@EnableBatchProcessing
public class ReaderConf
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

    public JdbcCursorItemReader<String> jdbcCursorItemReader()
    {
        JdbcCursorItemReader<String> reader = new JdbcCursorItemReader<>();

        reader.setSql("");
        reader.setDataSource(null);
        reader.setRowMapper(null);

        return reader;
    }

    public JdbcPagingItemReader<String> jdbcPagingItemReader()
    {
        JdbcPagingItemReader<String> reader = new JdbcPagingItemReader<>();

        reader.setFetchSize(10);

        reader.setDataSource(null);
        reader.setRowMapper(null);

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("");
        queryProvider.setFromClause("");


        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);

        return reader;
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
