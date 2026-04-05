package com.tradesphere.identity.controller;

import com.tradesphere.identity.dto.AuthRequestDTO;
import com.tradesphere.identity.dto.AuthResponseDTO;
import com.tradesphere.identity.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    final AuthService authService;
    @PostMapping("/register")
    public AuthResponseDTO register(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
        return authService.register(authRequestDTO);

    }
    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
        return authService.login(authRequestDTO );
    }
}
