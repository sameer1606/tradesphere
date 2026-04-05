package com.tradesphere.account.funds.repository;

import com.tradesphere.account.funds.domain.FundsLedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FundsLedgerRepository extends JpaRepository<FundsLedgerEntry, UUID> {
    List<FundsLedgerEntry> findByAccountProfileId(UUID accountProfileId);
    List<FundsLedgerEntry> findByReferenceId(UUID referenceId);
}
