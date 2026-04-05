package com.tradesphere.account.funds.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "funds_reservation")
public class FundsReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "account_profile_id", nullable = false)
    private UUID accountProfileId;
    @Column(name = "order_id", nullable = false)
    private UUID orderId;
    @Column(name = "amount", nullable = false, precision = 18, scale = 4)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReservationStatus status;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "released_at")
    private LocalDateTime releasedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
