package com.luxoft.orchestration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

//@Configuration
public class CustomStopConfig extends DefaultBatchConfigurer implements ApplicationContextAware
{

    public static final int TIMEOUT = 1000;
    private ApplicationContext applicationContext;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRegistry jobRegistry;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobLauncher jobLauncher;

    @Bean
    @StepScope
    public Tasklet tasklet(@Value("#{jobParameters['name']}") String name)
    {
        return (stepContribution, chunkContext) ->
        {
            System.out.println(String.format("==================>>>>>>> %s slipping...", name));
            Thread.sleep(TIMEOUT);
            return RepeatStatus.CONTINUABLE;
        };
    }

    @Bean
    public Job customJob()
    {
        return jobBuilderFactory.get("customJob")
                .start(stepBuilderFactory
                        .get("step")
                        .tasklet(tasklet(null))
                        .build())
                .build();
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistrar() throws Exception
    {
        JobRegistryBeanPostProcessor registrar = new JobRegistryBeanPostProcessor();

        registrar.setJobRegistry(jobRegistry);
        registrar.setBeanFactory(this.applicationContext.getAutowireCapableBeanFactory());
        registrar.afterPropertiesSet();

        return registrar;
    }

    @Bean
    public JobOperator jobOperator() throws Exception
    {
        SimpleJobOperator jobOperator = new SimpleJobOperator();

        jobOperator.setJobLauncher(jobLauncher);
        jobOperator.setJobParametersConverter(new DefaultJobParametersConverter());
        jobOperator.setJobRegistry(jobRegistry);
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobExplorer(jobExplorer);

        jobOperator.afterPropertiesSet();

        return jobOperator;
    }

    @Override
    public JobLauncher getJobLauncher()
    {
        SimpleJobLauncher launcher = new SimpleJobLauncher();

        launcher.setJobRepository(jobRepository);
        launcher.setTaskExecutor(new SimpleAsyncTaskExecutor());

        try
        {
            launcher.afterPropertiesSet();
        }
        catch (Exception ignore) {}

        return launcher;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
}
