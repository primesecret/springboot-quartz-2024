package com.finpong.quartz.dto;

import lombok.Data;

@Data
public class PaymentResponseDto {
    private int status;
    private String statusMessage;
    private Long jobId;
}
