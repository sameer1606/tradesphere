package com.tradesphere.order.docusign.repository;

import com.tradesphere.order.docusign.domain.SignatureTracking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SignatureTrackingRepository extends JpaRepository<SignatureTracking, UUID> {
    Optional<SignatureTracking> findByEnvelopeId(String envelopeId);
}
