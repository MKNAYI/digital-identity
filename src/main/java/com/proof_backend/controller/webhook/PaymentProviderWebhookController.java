package com.proof_backend.controller.webhook;

import com.proof_backend.service.webhook.PaymentProviderWebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PaymentProviderWebhookController {

    @Autowired
    private PaymentProviderWebhookService paymentProviderWebhookService;

    @PostMapping("/webhook/stripe/events")
    public ResponseEntity<?> handlerWebhookEvents(@RequestBody Object webhookEvents) {
        paymentProviderWebhookService.handlerWebhookEvents(webhookEvents);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
