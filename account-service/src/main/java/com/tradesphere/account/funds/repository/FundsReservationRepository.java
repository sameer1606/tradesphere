package com.tradesphere.account.funds.repository;

import com.tradesphere.account.funds.domain.FundsReservation;
import com.tradesphere.account.funds.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FundsReservationRepository extends JpaRepository<FundsReservation, UUID> {
    Optional<FundsReservation> findByOrderId(UUID orderId);
    List<FundsReservation> findByAccountProfileIdAndStatus(UUID accountProfileId, ReservationStatus status);
}
