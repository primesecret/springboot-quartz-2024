package com.finpong.quartz.job;

import java.util.Optional;

import com.finpong.quartz.entity.Payment;
import com.finpong.quartz.repository.PaymentRepository;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class PaymentJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentJob.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("Job starting...");
        /* Get message id recorded by scheduler during scheduling */
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        String paymentId = dataMap.getString("paymentId");

        LOGGER.info("Job PaymentId: {}", paymentId);

        /* Get message from database by id */
        Optional<Payment> messageOpt = paymentRepository.findById(Long.parseLong(paymentId));

        /* Run payment logic here and update the table */
        Payment message = messageOpt.get();
        message.setActive(false);
        message.setStatus(0);
        paymentRepository.save(message);

        /* unschedule or delete after job gets executed */

        try {
            context.getScheduler().deleteJob(new JobKey(paymentId));

            TriggerKey triggerKey = new TriggerKey(paymentId);

            context.getScheduler().unscheduleJob(triggerKey);

        } catch (SchedulerException e) {
            LOGGER.error("Error while running job id: {} : {}", paymentId, e);
            e.printStackTrace();
        }
        LOGGER.info("Job complete...");
    }
}
