package com.example.stockfeed.Dto;

import lombok.Data;

@Data

public class UserUpdateDto {
    private String name;
    private String password;
    private String profileImage;
    private String profileText;

}
