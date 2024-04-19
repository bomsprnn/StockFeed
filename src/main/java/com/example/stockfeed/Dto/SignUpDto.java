package com.example.stockfeed.Dto;

import lombok.Builder;
import lombok.Data;

@Data
public class SignUpDto {

    private String email;
    private String password;
    private String name;
    private String profileImage;
    private String profileText;

    @Builder
    public SignUpDto(String email, String password, String name, String profileImage, String profileText) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.profileImage = profileImage;
        this.profileText = profileText;
    }

}
