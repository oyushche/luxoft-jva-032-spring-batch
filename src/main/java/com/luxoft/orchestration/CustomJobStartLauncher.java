package com.luxoft.orchestration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//@SpringBootApplication
//@EnableBatchProcessing
//@EnableScheduling
public class CustomJobStartLauncher
{

//    @Autowired
//    private JobLauncher jobLauncher;

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private Job customJob;

    public static void main(String[] args) throws Exception
    {
        SpringApplication.run(CustomJobStartLauncher.class, args);
    }

//    @Scheduled(fixedDelay = 5000)
//    public void runAutowiredJobAndPassParamToIt() throws Exception {
//        String input;
//        while (!(input = new BufferedReader(new InputStreamReader(System.in)).readLine()).equals("q"))
//        {
//            JobParameters parameters = new JobParametersBuilder().addString("name", input).toJobParameters();
//            jobLauncher.run(customJob, parameters);
//        }
//    }

    @Scheduled(fixedDelay = 5000)
    public void runJobByNameAndPassParamToIt() throws Exception {
        String input;
        while (!(input = new BufferedReader(new InputStreamReader(System.in)).readLine()).equals("q"))
        {
            jobOperator.start("customJob", String.format("name=%s", input));
        }
    }

}
