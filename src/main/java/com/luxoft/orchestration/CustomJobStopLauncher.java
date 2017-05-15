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
public class CustomJobStopLauncher
{

    @Autowired
    private JobOperator jobOperator;

    public static void main(String[] args) throws Exception
    {
        SpringApplication.run(CustomJobStopLauncher.class, args);
    }

    public static boolean running = false;

    @Scheduled(fixedDelay = 1000)
    public void stopRunningJobByName() throws Exception {

        if (running)
        {
            return;
        }

        running = true;

        long id = jobOperator.start("customJob", String.format("name=%s", "customJob"));
        System.out.println(String.format("------------->>>>> job id: %d", id));


        String input;
        while (!(input = new BufferedReader(new InputStreamReader(System.in)).readLine()).equals("q"))
        {
            jobOperator.stop(Long.valueOf(input));
        }
    }

}
