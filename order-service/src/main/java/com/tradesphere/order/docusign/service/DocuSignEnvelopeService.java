package com.tradesphere.order.docusign.service;

import com.tradesphere.order.docusign.config.DocuSignProperties;
import com.tradesphere.order.docusign.domain.SignatureTracking;
import com.tradesphere.order.docusign.repository.SignatureTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DocuSignEnvelopeService {
    final DocuSignAuthService docuSignAuthService;
    private final WebClient webClient;
    private final DocuSignProperties docuSignProperties;
    final SignatureTrackingRepository signatureTrackingRepository;

    public DocuSignEnvelopeService(DocuSignAuthService docuSignAuthService, @Qualifier("docusignWebClient") WebClient webClient, DocuSignProperties docuSignProperties, SignatureTrackingRepository signatureTrackingRepository) {
        this.docuSignAuthService = docuSignAuthService;
        this.webClient = webClient;
        this.docuSignProperties = docuSignProperties;
        this.signatureTrackingRepository = signatureTrackingRepository;
    }

    public String createEnvolope(UUID orderId, String recipientEmail, String recipientName, BigDecimal orderAmount) {
        String accessToken = docuSignAuthService.getAccessToken();
        Map<String, Object> envelopeRequest = new HashMap<>();

        // Document
        Map<String, Object> document = new HashMap<>();
        document.put("documentBase64", Base64.getEncoder().encodeToString(
                ("Order Agreement - Order ID: " + orderId + " Amount: " + orderAmount + "\n\nSignature:").getBytes()));
        document.put("name", "Order Agreement");
        document.put("fileExtension", "txt");
        document.put("documentId", "1");

        //Signer
        Map<String, Object> signer = new HashMap<>();
        signer.put("email", recipientEmail);
        signer.put("name", recipientName);

        signer.put("routingOrder", "1");
        signer.put("recipientId", "1");
//        signer.put("clientUserId", "1");

        // Recipient
        Map<String, Object> recipients = new HashMap<>();
        recipients.put("signers", List.of(signer));

        //Where To Sign
        Map<String, Object> signHereTab = new HashMap<>();
        signHereTab.put("anchorString", "Signature:");
        signHereTab.put("anchorYOffset", "10");
        signHereTab.put("anchorUnits", "pixels");

        //Boxes where to sign
        Map<String, Object> tabs = new HashMap<>();
        tabs.put("signHereTabs", List.of(signHereTab));

        signer.put("tabs", tabs);
        //Final request Body
        envelopeRequest.put("emailSubject", "Please sign your Order Agreement");
        envelopeRequest.put("status", "sent");
        envelopeRequest.put("documents", List.of(document));
        envelopeRequest.put("recipients", recipients);


        var envelopeId = webClient.post()
                .uri("/restapi/v2.1/accounts/{accountId}/envelopes", docuSignProperties.getAccountId())
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(envelopeRequest)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("envelopeId"))
                .block();

        SignatureTracking tracking = SignatureTracking.builder()
                .orderId(orderId)
                .envelopeId(envelopeId)
                .signerEmail(recipientEmail)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        signatureTrackingRepository.save(tracking);

        return envelopeId;

    }

    public String getSigningUrl(String envelopeId, String recipientEmail, String recipientName) {
        return webClient.post()
                .uri("/restapi/v2.1/accounts/{accountId}/envelopes/{envelopeId}/views/recipient", docuSignProperties.getAccountId(), envelopeId)
                .header("Authorization", "Bearer " + docuSignAuthService.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "authenticationMethod", "none",
                        "clientUserId", "1",
                        "recipientId", "1",
                        "returnUrl", "https://localhost:8084/docusign/complete",
                        "userName", recipientName,
                        "email", recipientEmail
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("url"))
                .block();

    }
}
