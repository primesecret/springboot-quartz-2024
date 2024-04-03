package com.finpong.quartz.dto;

import lombok.Data;

@Data
public class PaymentRequestDto {
    private String senderId;
    private String receiverId;
    private double amount;
    private double fee;
    private String paymentScheduleAt;
}
