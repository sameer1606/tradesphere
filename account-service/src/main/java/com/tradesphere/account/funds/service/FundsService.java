package com.tradesphere.account.funds.service;

import com.tradesphere.account.exception.*;
import com.tradesphere.account.funds.domain.*;
import com.tradesphere.account.funds.repository.FundsLedgerRepository;
import com.tradesphere.account.funds.repository.FundsReservationRepository;
import com.tradesphere.account.funds.repository.FundsWalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class FundsService {
    private final FundsWalletRepository walletRepository;
    private final FundsReservationRepository reservationRepository;
    private final FundsLedgerRepository ledgerRepository;

    @Transactional
    public void reserve(UUID accountProfileId, UUID orderId, BigDecimal amount) {
        try {
            Optional<FundsReservation> existingReservation = reservationRepository.findByOrderId(orderId);
            if (existingReservation.isPresent()) {
                if (existingReservation.get().getStatus() == ReservationStatus.ACTIVE
                        && existingReservation.get().getAmount().compareTo(amount) == 0) {
                    log.info("Reservation already exists for orderId: {}, returning early", orderId);
                    return;
                } else if (existingReservation.get().getStatus() == ReservationStatus.ACTIVE
                        && existingReservation.get().getAmount().compareTo(amount) != 0) {
                    throw new DuplicateReservationConflictException(orderId);
                } else if (existingReservation.get().getStatus() == ReservationStatus.RELEASED
                        || existingReservation.get().getStatus() == ReservationStatus.SETTLED) {
                    throw new ReservationAlreadyClosedException(orderId);
                }
            }

            FundsWallet wallet = walletRepository.findByAccountProfileId(accountProfileId)
                    .orElseThrow(() -> new WalletNotFoundException(accountProfileId));

            if (wallet.getAvailableFunds().compareTo(amount) < 0) {
                throw new InsufficientFundsException(accountProfileId);
            }
            wallet.setAvailableFunds(wallet.getAvailableFunds().subtract(amount));
            wallet.setBlockedFunds(wallet.getBlockedFunds().add(amount));
            walletRepository.save(wallet);

            FundsReservation newReservation = createReservation(accountProfileId, orderId, amount);
            createBlockLedgerEntry(accountProfileId, wallet, newReservation, amount);
        }catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Optimistic locking failure while reserving funds for orderId={}, retrying operation", orderId);
            throw new ConcurrencyConflictException("Concurrent update detected, please retry the operation");
        }
    }

    @Transactional
    public void release(UUID orderId) {
        try {
            FundsReservation reservation = reservationRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new ReservationNotFoundException(orderId));

            if (reservation.getStatus() == ReservationStatus.RELEASED) {
                log.info("Reservation already released for orderId={}, returning early", orderId);
                return;
            }
            if (reservation.getStatus() == ReservationStatus.SETTLED) {
                throw new ReservationAlreadyClosedException(orderId);
            }

            FundsWallet wallet = walletRepository.findByAccountProfileId(reservation.getAccountProfileId())
                    .orElseThrow(() -> new WalletNotFoundException(reservation.getAccountProfileId()));

            wallet.setAvailableFunds(wallet.getAvailableFunds().add(reservation.getAmount()));
            wallet.setBlockedFunds(wallet.getBlockedFunds().subtract(reservation.getAmount()));
            walletRepository.save(wallet);

            reservation.setStatus(ReservationStatus.RELEASED);
            reservation.setReleasedAt(LocalDateTime.now());
            reservationRepository.save(reservation);
            createUnblockLedgerEntry(reservation, wallet.getAvailableFunds());
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Optimistic locking failure while reserving funds for orderId={}, retrying operation", orderId);
            throw new ConcurrencyConflictException("Concurrent update detected, please retry the operation");
        }
    }

    @Transactional
    public void finalizeReservation(UUID orderId) {
        try {
            FundsReservation reservation = reservationRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new ReservationNotFoundException(orderId));

            if (reservation.getStatus() == ReservationStatus.SETTLED) {
                log.info("Reservation already settled for orderId={}", orderId);
                return;
            }
            if (reservation.getStatus() == ReservationStatus.RELEASED) {
                throw new ReservationAlreadyClosedException(orderId);
            }

            FundsWallet wallet = walletRepository.findByAccountProfileId(reservation.getAccountProfileId())
                    .orElseThrow(() -> new WalletNotFoundException(reservation.getAccountProfileId()));

            wallet.setBlockedFunds(wallet.getBlockedFunds().subtract(reservation.getAmount()));
            walletRepository.save(wallet);

            reservation.setStatus(ReservationStatus.SETTLED);
            reservationRepository.save(reservation);
            createSettleLedgerEntry(reservation, wallet.getAvailableFunds());
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Optimistic locking failure while reserving funds for orderId={}, retrying operation", orderId);
            throw new ConcurrencyConflictException("Concurrent update detected, please retry the operation");
        }
    }

    private FundsReservation createReservation(UUID accountProfileId, UUID orderId, BigDecimal amount) {
        FundsReservation newReservation = FundsReservation.builder()
                .accountProfileId(accountProfileId)
                .orderId(orderId)
                .amount(amount)
                .status(ReservationStatus.ACTIVE)
                .build();
        return reservationRepository.save(newReservation);
    }

    private void createBlockLedgerEntry(UUID accountProfileId, FundsWallet wallet, FundsReservation newReservation, BigDecimal amount) {
        FundsLedgerEntry ledgerEntry = FundsLedgerEntry.builder()
                .accountProfileId(accountProfileId)
                .amount(amount)
                .balanceAfter(wallet.getAvailableFunds())
                .transactionType(TransactionType.BLOCK)
                .referenceId(newReservation.getOrderId())
                .description("Funds reserved for orderId: " + newReservation.getOrderId())
                .build();
        ledgerRepository.save(ledgerEntry);
    }

    private void createUnblockLedgerEntry(FundsReservation reservation, BigDecimal availableFunds) {
        FundsLedgerEntry entry = FundsLedgerEntry.builder()
                .accountProfileId(reservation.getAccountProfileId())
                .referenceId(reservation.getOrderId())
                .transactionType(TransactionType.UNBLOCK)
                .amount(reservation.getAmount())
                .balanceAfter(availableFunds)
                .description("Funds released for orderId: " + reservation.getOrderId())
                .build();
        ledgerRepository.save(entry);
    }

    private void createSettleLedgerEntry(FundsReservation reservation, BigDecimal availableFunds) {
        FundsLedgerEntry entry = FundsLedgerEntry.builder()
                .accountProfileId(reservation.getAccountProfileId())
                .referenceId(reservation.getOrderId())
                .transactionType(TransactionType.SETTLE)
                .amount(reservation.getAmount())
                .balanceAfter(availableFunds)
                .description("Funds settled for orderId: " + reservation.getOrderId())
                .build();
        ledgerRepository.save(entry);
    }
}