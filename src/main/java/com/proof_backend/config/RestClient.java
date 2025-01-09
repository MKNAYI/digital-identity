package com.proof_backend.config;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class RestClient {

    private final RestTemplate restTemplate;

    @Autowired
    public RestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // GET Request with Headers
    public <T> T get(String url, HttpHeaders headers, Class<T> responseType) {
        HttpEntity<Void> entity = new HttpEntity<>(headers);  // Create HttpEntity with headers
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
        return response.getBody();
    }

    // POST Request with Headers
    public <T> T post(String url, Object requestBody, HttpHeaders headers, Class<T> responseType) {
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);  // Create HttpEntity with body and headers
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
        return response.getBody();
    }

    // PUT Request with Headers
    public <T> T put(String url, Object requestBody, HttpHeaders headers, Class<T> responseType) {
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);  // Create HttpEntity with body and headers
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
        return response.getBody();
    }

    // DELETE Request with Headers
    public void delete(String url, HttpHeaders headers) {
        HttpEntity<Void> entity = new HttpEntity<>(headers);  // Create HttpEntity with headers
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }
}
