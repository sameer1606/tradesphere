package com.tradesphere.order.docusign.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "docusign")
public class DocuSignProperties {

    private String accountId;
    private String userId;
    private String integrationKey;
    private String baseUrl;
    private String privateKeyPath;


}
