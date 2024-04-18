package com.example.stockfeed.Dto;

import lombok.Data;

@Data
public class MailAuthDto { //이메일 인증을 위한 DTO
    private String receiver;
    private int number;
}
