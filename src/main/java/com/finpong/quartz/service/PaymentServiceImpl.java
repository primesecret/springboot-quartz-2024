package com.finpong.quartz.service;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import com.finpong.quartz.config.QuartzConfig;
import com.finpong.quartz.dto.PaymentRequestDto;
import com.finpong.quartz.dto.PaymentResponseDto;
import com.finpong.quartz.entity.Payment;
import com.finpong.quartz.job.PaymentJob;
import com.finpong.quartz.repository.PaymentRepository;
import com.finpong.quartz.util.CommonUtils;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private QuartzConfig quartzConfig;
    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    @Override
    public PaymentResponseDto schedulePayment(PaymentRequestDto paymentRequest) {

        PaymentResponseDto response = new PaymentResponseDto();

        try {
            // Scheduling time to run job
            Date triggerJobAt = CommonUtils.convertStringToDate(paymentRequest.getPaymentScheduleAt());

            // save messages in table
            Payment payment = Payment.builder().senderId(paymentRequest.getSenderId())
                    .receiverId(paymentRequest.getReceiverId()).amount(paymentRequest.getAmount())
                    .fee(paymentRequest.getFee()).paymentScheduleAt(triggerJobAt).status(1).build();

            payment = paymentRepository.save(payment);

            // Creating JobDetail instance
            String paymentId = String.valueOf(payment.getId());
            JobDetail jobDetail = JobBuilder.newJob(PaymentJob.class).withIdentity(paymentId).build();

            // Adding JobDataMap to jobDetail
            jobDetail.getJobDataMap().put("paymentId", paymentId);

            SimpleTrigger trigger =
                    TriggerBuilder.newTrigger().withIdentity(paymentId).startAt(triggerJobAt)
                            .withSchedule(
                                    SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                            .build();
            // Getting scheduler instance
            Scheduler scheduler = quartzConfig.schedulerFactoryBean().getScheduler();
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
            response.setStatus(0);
            response.setJobId(payment.getId());
            response.setStatusMessage("Successfully scheduled.");
        } catch (IOException | SchedulerException e) {
            // scheduling failed
            response.setStatus(-1);
            response.setStatusMessage("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    @Transactional
    @Override
    public PaymentResponseDto unschedulePayment(Long paymentId) {
        PaymentResponseDto response = new PaymentResponseDto();

        Optional<Payment> existingPaymentOpt = paymentRepository.findById(paymentId);
        if (!existingPaymentOpt.isPresent()) {
            response.setStatus(-1);
            response.setStatusMessage("Payment Not Found");
            return response;
        }

        Payment payment = existingPaymentOpt.get();
        payment.setActive(false); // deactivating
        payment.setStatus(-2); // -2 to indicate cancel
        paymentRepository.save(payment);

        try {
            Scheduler scheduler = quartzConfig.schedulerFactoryBean().getScheduler();
            String id = String.valueOf(paymentId);
            scheduler.deleteJob(new JobKey(id));
            TriggerKey triggerKey = new TriggerKey(id);
            scheduler.unscheduleJob(triggerKey);
            response.setStatus(0); // 0 to indicate success
            response.setStatusMessage("Successfully unscheduled.");

        } catch (IOException | SchedulerException e) {
            response.setStatus(-1);
            response.setStatusMessage("Fail to unschedule. Error: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

}