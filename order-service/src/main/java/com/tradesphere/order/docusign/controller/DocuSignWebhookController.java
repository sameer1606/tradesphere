package com.tradesphere.order.docusign.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradesphere.order.docusign.repository.SignatureTrackingRepository;
import com.tradesphere.order.docusign.service.DocuSignWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/docusign/webhook")
@RequiredArgsConstructor
public class DocuSignWebhookController {
    final DocuSignWebhookService docuSignWebhookService;

    @PostMapping
    public ResponseEntity<Void> receiveEnvelope(@RequestBody Map<String, Object> payload) {
        docuSignWebhookService.processWebhookEvent(payload);
        return ResponseEntity.ok().build();
    }
}
