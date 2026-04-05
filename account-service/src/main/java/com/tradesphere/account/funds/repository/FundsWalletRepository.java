package com.tradesphere.account.funds.repository;

import com.tradesphere.account.funds.domain.FundsWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FundsWalletRepository extends JpaRepository<FundsWallet, UUID> {
    Optional<FundsWallet> findByAccountProfileId(UUID userId);
}
