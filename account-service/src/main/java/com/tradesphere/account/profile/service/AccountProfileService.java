package com.tradesphere.account.profile.service;

import com.tradesphere.account.exception.ProfileNotFoundException;
import com.tradesphere.account.profile.domain.AccountProfile;
import com.tradesphere.account.profile.repository.AccountProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountProfileService {
    final AccountProfileRepository accountProfileRepository;

    public AccountProfile getProfile(UUID userId) {
        return accountProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found for userId: " + userId));
    }
}