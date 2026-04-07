package com.tradesphere.order.docusign.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import java.util.UUID;

@Entity
@Table(name = "signature_tracking")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignatureTracking {

    @Id
    @GeneratedValue
    private UUID id;
    private UUID orderId;
    private String envelopeId;
    private String signerEmail;
    private String status;
    private String signingUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
