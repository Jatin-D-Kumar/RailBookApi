package com.railbook.api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.railbook.api.dto.request.LoginReqDto;
import com.railbook.api.dto.request.RegisterDto;
import com.railbook.api.dto.response.AuthResponse;
import com.railbook.api.dto.response.LoginResDto;
import com.railbook.api.service.AuthService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    private ResponseEntity<String> registerUser(@Valid @RequestBody RegisterDto registerDto) {
        return ResponseEntity.ok(authService.registerUser(registerDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResDto> login(@Valid @RequestBody LoginReqDto dto) {
        AuthResponse authResponse = authService.loginUser(dto);

        LoginResDto loginResDto = new LoginResDto();
        loginResDto.setAccessToken(authResponse.getAccessToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResponse.getResponseCookie().toString())
                .body(loginResDto);
    }

}
