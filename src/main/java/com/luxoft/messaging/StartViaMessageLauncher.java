package com.luxoft.messaging;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

//@SpringBootApplication
//@EnableBatchProcessing
//@EnableScheduling
public class StartViaMessageLauncher
{
    @Autowired
    private MessageChannel requests;

    @Autowired
    private DirectChannel replies;

    @Autowired
    private Job messageJob;

    public static void main(String[] args) throws Exception
    {
        SpringApplication.run(StartViaMessageLauncher.class, args);
    }

    @Scheduled(fixedDelay = 5000)
    public void runJobViaMessage() throws Exception
    {
        JobLaunchRequest request = new JobLaunchRequest(messageJob, new JobParametersBuilder().toJobParameters());

        replies.subscribe((msg) ->
        {
            JobExecution execution = (JobExecution) msg.getPayload();

            System.out.println("--------------->>>>>>>>>>>> "
                    + execution.getJobInstance().getJobName() + " requested in " + execution.getStatus());
        });

        requests.send(MessageBuilder.withPayload(request).setReplyChannel(replies).build());
    }
}
