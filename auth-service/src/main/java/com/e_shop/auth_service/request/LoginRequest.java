package com.e_shop.auth_service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.NotNull;


@Data
@AllArgsConstructor
public class LoginRequest {
    @NotNull(message = "username required")
    private String username;
    @NotNull(message = "password required")
    private String password;
}

