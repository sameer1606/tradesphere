package com.tradesphere.order.docusign.service;

import com.tradesphere.order.docusign.config.DocuSignProperties;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class DocuSignAuthService {
    private final WebClient webClient;
    private final ResourceLoader resourceLoader;
    private final DocuSignProperties docuSignProperties;

    public DocuSignAuthService(@Qualifier("docusignWebClient") WebClient webClient, ResourceLoader resourceLoader, DocuSignProperties docuSignProperties) {
        this.webClient = webClient;
        this.resourceLoader = resourceLoader;
        this.docuSignProperties = docuSignProperties;
    }

    public String getAccessToken() {
        PrivateKey privateKey = loadPrivateKey();
        String jwt = buildJwt(privateKey);
        return exchangeJwtForToken(jwt);
    }

    private PrivateKey loadPrivateKey() {
        Resource resource = resourceLoader.getResource(docuSignProperties.getPrivateKeyPath());
        try (InputStream is = resource.getInputStream()) {
            PEMParser pemParser = new PEMParser(new StringReader(new String(is.readAllBytes())));
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            Object pemObject = pemParser.readObject();

            PrivateKey privateKey;
            if (pemObject instanceof PEMKeyPair pemKeyPair) {
                privateKey = converter.getPrivateKey(pemKeyPair.getPrivateKeyInfo());
            } else {
                privateKey = converter.getPrivateKey((PrivateKeyInfo) pemObject);
            }
            return privateKey;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load private key", e);
        }
    }

    private String buildJwt(PrivateKey privateKey) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", docuSignProperties.getIntegrationKey());
        claims.put("sub", docuSignProperties.getUserId());
        claims.put("aud", "account-d.docusign.com");
        claims.put("iat", Instant.now().getEpochSecond());
        claims.put("exp", Instant.now().plusSeconds(3600).getEpochSecond());
        claims.put("scope", "signature impersonation");

        return Jwts.builder()
                .claims(claims)
                .signWith(privateKey)
                .compact();
    }


    private String exchangeJwtForToken(String jwt) {
        Map response = webClient.post()
                .uri("https://account-d.docusign.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                        .with("assertion", jwt))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return (String) response.get("access_token");
    }
}