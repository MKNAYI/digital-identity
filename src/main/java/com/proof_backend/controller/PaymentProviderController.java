package com.proof_backend.controller;

import com.proof_backend.model.SubscriptionRequestDTO;
import com.proof_backend.model.SubscriptionUpdateRequestDTO;
import com.proof_backend.model.UserSubscriptionDTO;
import com.proof_backend.service.PaymentProviderService;
import com.stripe.exception.StripeException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PaymentProviderController {
    @Autowired
    private PaymentProviderService paymentProviderService;

    @PostMapping("/users/{user-id}/subscriptions")
    public ResponseEntity<?> createPaymentProviderSubscription(@PathVariable("user-id") Long userId, @RequestBody SubscriptionRequestDTO subscriptionRequest) throws StripeException {
        paymentProviderService.createPaymentProviderSubscription(userId, subscriptionRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/users/{user-id}/subscriptions/payments")
    public ResponseEntity<?> changePaymentProviderSubscriptionPaymentMethod(@PathVariable("user-id") Long userId, @RequestBody SubscriptionRequestDTO subscriptionRequest) throws StripeException {
        paymentProviderService.changePaymentProviderSubscriptionPaymentMethod(userId, subscriptionRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/users/{user-id}/subscriptions")
    public ResponseEntity<UserSubscriptionDTO> cancelPaymentProviderSubscription(@PathVariable("user-id") Long userId, @RequestBody SubscriptionUpdateRequestDTO updateRequestDTO) throws StripeException, BadRequestException {
        return new ResponseEntity<UserSubscriptionDTO>(paymentProviderService.cancelPaymentProviderSubscription(userId, updateRequestDTO), HttpStatus.OK);
    }
}
