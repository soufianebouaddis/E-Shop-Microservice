package com.e_shop.auth_service.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {
    private String accessToken;
    private String refreshToken;

    public JwtResponse(String accesstoken) {
        this.accessToken = accesstoken;
        this.refreshToken = null;
    }

    public JwtResponse(String accesstoken, String refreshToken) {
        this.accessToken = accesstoken;
        this.refreshToken = refreshToken;
    }

}
