package com.railbook.api.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.railbook.api.dto.request.LoginReqDto;
import com.railbook.api.dto.request.RegisterDto;
import com.railbook.api.dto.response.AuthResponse;
import com.railbook.api.entity.RefreshToken;
import com.railbook.api.entity.User;
import com.railbook.api.exception.InvalidCredentialsException;
import com.railbook.api.exception.UserNotFoundException;
import com.railbook.api.repository.RefreshTokenRepository;
import com.railbook.api.repository.UserRepository;
import com.railbook.api.security.JwtUtil;
import com.railbook.api.service.AuthService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public String registerUser(RegisterDto registerDto) {

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User userEntity = new User();
        userEntity.setName(registerDto.getName());
        userEntity.setEmail(registerDto.getEmail());
        userEntity.setPhone(registerDto.getPhone().replaceAll("^\\+91", "").replaceAll("^91", ""));
        userEntity.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        userRepository.save(userEntity);

        return "User Registered Successfully";
    }

    @Override
    @Transactional
    public AuthResponse loginUser(LoginReqDto loginReqDto) {
        User user = userRepository.findByEmail(loginReqDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(loginReqDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId().toString());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId().toString());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(accessToken);

        RefreshToken token = new RefreshToken();
        token.setToken(refreshToken);
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(token);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/auth/refresh")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        authResponse.setResponseCookie(cookie);

        return authResponse;
    }

}
