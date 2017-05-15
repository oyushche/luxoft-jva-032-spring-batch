package com.luxoft.p6listeners;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class JobListener implements JobExecutionListener
{

    @Override
    public void beforeJob(JobExecution jobExecution)
    {
        System.out.println("===>>> before: " + jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution)
    {
        System.out.println("===>>> after: " + jobExecution.getJobInstance().getJobName());
    }

}
