package com.railbook.api.dto.response;

import org.springframework.http.ResponseCookie;

import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private ResponseCookie responseCookie;
}
