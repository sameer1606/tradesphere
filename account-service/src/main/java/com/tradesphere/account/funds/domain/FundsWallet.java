package com.tradesphere.account.funds.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "funds_wallet")
public class FundsWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "account_profile_id", nullable = false)
    private UUID accountProfileId;
    @Column(name = "available_funds", nullable = false, precision = 18, scale = 4)
    private BigDecimal availableFunds;
    @Column(name = "blocked_funds", nullable = false, precision = 18, scale = 4)
    private BigDecimal blockedFunds;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Version
    private Integer version;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
