package com.tradesphere.account.profile.controller;

import com.tradesphere.account.profile.domain.AccountProfile;
import com.tradesphere.account.profile.service.AccountProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AccountProfileController {

    private final AccountProfileService accountProfileService;

    @GetMapping("/me")
    public ResponseEntity<AccountProfile> getMyProfile(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(accountProfileService.getProfile(userId));
    }
}