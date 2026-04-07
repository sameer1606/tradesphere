package com.tradesphere.order.docusign.controller;

import com.tradesphere.order.docusign.dto.CreateEnvelopeDto;
import com.tradesphere.order.docusign.dto.SigningUrlDto;
import com.tradesphere.order.docusign.service.DocuSignEnvelopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/docusign")
@RequiredArgsConstructor
public class DocuSignController {
    final DocuSignEnvelopeService docuSignEnvelopeService;

    @PostMapping("/send")
    public String createDocuSignEnvelope(@RequestBody CreateEnvelopeDto createEnvelopeDto) {
        return    docuSignEnvelopeService.createEnvolope(
                createEnvelopeDto.orderId(),
                createEnvelopeDto.recipientEmail(),
                createEnvelopeDto.recipientName(),
                createEnvelopeDto.orderAmount()
        );


    }
    @PostMapping ("/sign/url")
    public String getSigningUrl(@RequestBody SigningUrlDto signingUrlDto) {
        return docuSignEnvelopeService.getSigningUrl(
                signingUrlDto.envelopeId(),
                signingUrlDto.recipientEmail(),
                signingUrlDto.recipientName()
        );

    }



}
