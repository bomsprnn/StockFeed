package com.example.stockfeed.Dto;

import lombok.Data;

@Data
public class SignUpDto {

    private String email;
    private String password;
    private String name;
    private String profileImage;
    private String profileText;
}
