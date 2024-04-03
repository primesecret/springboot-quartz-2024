package com.finpong.quartz.service;

import com.finpong.quartz.dto.PaymentRequestDto;
import com.finpong.quartz.dto.PaymentResponseDto;

public interface PaymentService {

    PaymentResponseDto schedulePayment(PaymentRequestDto paymentRequest);

    PaymentResponseDto unschedulePayment(Long paymentId);
}