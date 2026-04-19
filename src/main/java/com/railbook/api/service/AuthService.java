package com.railbook.api.service;

import com.railbook.api.dto.request.LoginReqDto;
import com.railbook.api.dto.request.RegisterDto;
import com.railbook.api.dto.response.AuthResponse;

public interface AuthService {
    String registerUser(RegisterDto registerDto);

    AuthResponse loginUser(LoginReqDto loginReqDto);
}
