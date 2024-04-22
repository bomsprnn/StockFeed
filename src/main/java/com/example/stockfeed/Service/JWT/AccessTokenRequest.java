package com.example.stockfeed.Service.JWT;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AccessTokenRequest {
    private String accessToken;
    public String getRefreshToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}