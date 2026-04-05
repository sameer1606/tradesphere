package com.tradesphere.account.profile.repository;

import com.tradesphere.account.profile.domain.AccountProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountProfileRepository extends JpaRepository<AccountProfile, UUID> {
    Optional<AccountProfile> findByUserId(UUID userId);
}
