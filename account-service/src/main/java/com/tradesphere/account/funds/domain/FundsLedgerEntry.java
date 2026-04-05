package com.tradesphere.account.funds.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "funds_ledger")
public class FundsLedgerEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "account_profile_id" , nullable = false)
    private UUID accountProfileId;
    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @Column(name = "amount", nullable = false, precision = 18, scale = 4)
    private BigDecimal amount;
    @Column(name = "balance_after", nullable = false, precision = 18, scale = 4)
    private BigDecimal balanceAfter;
    @Column(name = "reference_id")
    private UUID referenceId;
    @Column(name = "description")
    private String description;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }


}
