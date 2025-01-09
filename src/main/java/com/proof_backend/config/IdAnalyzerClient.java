package com.proof_backend.config;

import com.proof_backend.model.idanalyzer.DocumentScanRequestDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.proof_backend.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IdAnalyzerClient {

    @Value("${idanalyzer.base-url}")
    private String idAnalyzerBaseUrl;

    @Value("${idanalyzer.api-key}")
    private String idAnalyzerApiKey;

    @Autowired
    private RestClient restClient;

    public JsonNode verifyDocuments(DocumentScanRequestDTO documentScanRequestDTO) {
        log.info("IdAnalyzerClient:: verifyDocuments() invoked");
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("Content-Type", "application/json");
        headers.set("X-API-KEY", idAnalyzerApiKey);
        String response = restClient.post(String.format("%s%s", idAnalyzerBaseUrl, "/scan"), documentScanRequestDTO, headers,String.class);
        log.info("IdAnalyzerClient:: verifyDocuments() :: response: {}", response);
        return Utils.stringToJsonNode(response);
    }
}
