package com.tradesphere.identity.service;

import com.tradesphere.identity.domain.User;
import com.tradesphere.identity.dto.AuthRequestDTO;
import com.tradesphere.identity.dto.AuthResponseDTO;
import com.tradesphere.identity.exception.DuplicateUserException;
import com.tradesphere.identity.exception.InvalidCredentialsException;
import com.tradesphere.identity.repository.UserRepository;
import com.tradesphere.identity.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
     final UserRepository userRepository;
     final JwtUtil jwtUtil;
     final PasswordEncoder passwordEncoder;

    public AuthResponseDTO register(AuthRequestDTO request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            throw new DuplicateUserException("User already exists");
        });
        var encodedPassword = passwordEncoder.encode(request.password());
        User user = User.builder()
                .email(request.email())
                .password(encodedPassword)
                .role("ROLE_USER")
                .build();
        userRepository.save(user);
        String jwt_token = jwtUtil.generateToken(user);


        return new AuthResponseDTO(jwt_token);
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }
        String jwt_token = jwtUtil.generateToken(user);
        return new AuthResponseDTO(jwt_token);
    }
}
