package com.finpong.quartz.controller;

import com.finpong.quartz.dto.PaymentRequestDto;
import com.finpong.quartz.dto.PaymentResponseDto;
import com.finpong.quartz.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping(path = "/schedule")
    public @ResponseBody PaymentResponseDto schedulePayment(
            @RequestBody PaymentRequestDto paymentRequestDto) {

        return paymentService.schedulePayment(paymentRequestDto);
    }

    @DeleteMapping(path = "/unschedule")
    public @ResponseBody PaymentResponseDto unschedulePayment(
            @PathVariable(name = "paymentId") Long paymentId) {

        return paymentService.unschedulePayment(paymentId);
    }

}