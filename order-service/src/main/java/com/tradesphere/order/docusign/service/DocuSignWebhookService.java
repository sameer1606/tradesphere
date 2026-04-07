package com.tradesphere.order.docusign.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradesphere.order.docusign.repository.SignatureTrackingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocuSignWebhookService {
    final SignatureTrackingRepository signatureTrackingRepository;
    final KafkaTemplate<String, String> kafkaTemplate;
    final ObjectMapper objectMapper;

    public void processWebhookEvent(Map<String, Object> payload) {
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        String envelopeId = (String) data.get("envelopeId");
        String eventType = (String) payload.get("event");

        var signatureTrackingOpt = signatureTrackingRepository.findByEnvelopeId(envelopeId);
        if (signatureTrackingOpt.isEmpty()) {
            throw new RuntimeException("Signature tracking not found for envelopeId: " + envelopeId);
        }
        log.info("Webhook payload: {}", payload);

        var signatureTracking = signatureTrackingOpt.get();

        switch (eventType) {
            case "envelope-voided" -> {
                signatureTracking.setStatus("VOIDED");
                signatureTracking.setUpdatedAt(LocalDateTime.now());
            }
            case "envelope-completed" -> {
                signatureTracking.setStatus("SIGNED");
                signatureTracking.setUpdatedAt(LocalDateTime.now());
            }
            case "envelope-declined" -> {
                signatureTracking.setStatus("DECLINED");
                signatureTracking.setUpdatedAt(LocalDateTime.now());
            }
            default -> log.warn("");
        }
        try {
            signatureTrackingRepository.save(signatureTracking);
            kafkaTemplate.send("docusign.events", objectMapper.writeValueAsString(payload));
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish event", e);
        }
    }


}
