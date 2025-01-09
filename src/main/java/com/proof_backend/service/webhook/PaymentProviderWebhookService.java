package com.proof_backend.service.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.proof_backend.entity.Transaction;
import com.proof_backend.entity.UserSubscription;
import com.proof_backend.enums.TransactionStatus;
import com.proof_backend.enums.UserStatus;
import com.proof_backend.enums.UserSubscriptionStatus;
import com.proof_backend.exceptions.ResourceNotFoundException;
import com.proof_backend.repository.TransactionRepository;
import com.proof_backend.repository.UserSubscriptionRepository;
import static com.proof_backend.utils.Constants.*;

import com.proof_backend.service.UserService;
import com.proof_backend.utils.Utils;
import com.stripe.Stripe;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentProviderWebhookService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    public void handlerWebhookEvents(Object webhookEvents) {
        Stripe.apiKey = stripeSecretKey;
        log.info("PaymentProviderWebhookService :: handlerWebhookEvents() invoked.");
        log.info("PaymentProviderWebhookService :: Webhook Events: {}", webhookEvents);
        String eventId = null;
        try {
            JsonNode rootNode = Utils.objectToJsonNode(webhookEvents);
            eventId = rootNode.path(STRIPE_WEB_HOOK_EVENT_TYPE_ATTRIBUTE_ID).textValue();
            log.info("Event Id: {}", eventId);
            String eventType = rootNode.path(STRIPE_WEB_HOOK_EVENT_TYPE_ATTRIBUTE_TYPE).textValue();
            log.info("Event type: {}, eventId: {}", eventType, eventId);
            JsonNode objectNode = rootNode.path(STRIPE_WEB_HOOK_EVENT_DATA_ATTRIBUTE_NAME).path(STRIPE_WEB_HOOK_EVENT_TYPE_DATA_OBJECT_ATTRIBUTE_NAME);
            String subscriptionId = objectNode.path(SUBSCRIPTION_ID).textValue();
            String status = objectNode.path(SUBSCRIPTION_STATUS).textValue();
            log.info("Status: {}, eventId: {}", status, eventId);
            Long currentPeriodStart = objectNode.path(SUBSCRIPTION_CURRENT_PERIOD_START).longValue();
            Long currentPeriodEnd = objectNode.path(SUBSCRIPTION_CURRENT_PERIOD_END).longValue();
            String invoiceId = objectNode.path(SUBSCRIPTION_LATEST_INVOICE_ATTRIBUTE_NAME).textValue();

            UserSubscription userSubscription = userSubscriptionRepository.findByPaymentProviderSubscriptionId(subscriptionId);
            if(userSubscription != null) {
                log.info("UserSubscription fetched successfully, eventId: {}", eventId);
                UserSubscriptionStatus userSubscriptionStatus = mapToUserSubscriptionStatus(status, eventType);
                log.info("UserSubscription fetched successfully, status: {}, eventId: {}", userSubscriptionStatus, eventId);
                if (userSubscriptionStatus.equals(UserSubscriptionStatus.ACTIVE)) {
                    userSubscription.setStatus(userSubscriptionStatus);
                    userSubscription.setStartDate(Utils.toCovertLongDateToLocalDateTime(currentPeriodStart));
                    userSubscription.setEndDate(Utils.toCovertLongDateToLocalDateTime(currentPeriodEnd));
                    userService.updateUserStatus(userSubscription.getUser().getId(), UserStatus.VERIFIED);
                    log.info("User updated successfully, status: {}, user-id:{}, eventId: {}", UserStatus.VERIFIED, userSubscription.getUser().getId(), eventId);
                } else {
                    userSubscription.setStatus(userSubscriptionStatus);
                }
                userSubscriptionRepository.save(userSubscription);
                log.info("UserSubscription updated successfully, eventId: {}", eventId);

                Invoice invoice = Invoice.retrieve(invoiceId);
                log.info("Invoice fetched successfully.");

                TransactionStatus transactionStatus = mapToTransactionStatus(invoice.getStatus(), status);
                log.info("TransactionStatus: {}", transactionStatus);
                Transaction transaction = transactionRepository.findTopByUserSubscriptionId(userSubscription.getId());
                if(transaction == null) {
                    transaction = mapToTransaction(userSubscription, invoiceId, transactionStatus, eventType, status, invoice);
                } else {
                    if (transaction.getStatus().equals(TransactionStatus.PENDING) ||
                            (transaction.getStatus().equals(transactionStatus) &&
                                    transaction.getWebhookEventType().equalsIgnoreCase(eventType) &&
                                    transaction.getPaymentProviderSubscriptionStatus().equalsIgnoreCase(status) &&
                                    transaction.getPaymentProviderInvoiceStatus().equalsIgnoreCase(invoice.getStatus()))) {
                        transaction.setStatus(transactionStatus);
                        transaction.setPaymentProviderTransactionId(invoiceId);
                        transaction.setPaymentMethodId(userSubscription.getPaymentMethodId());
                        transaction.setWebhookEventType(eventType);
                        transaction.setPaymentProviderSubscriptionStatus(status);
                        transaction.setPaymentProviderInvoiceStatus(invoice.getStatus());
                        transaction.setStartDate(userSubscription.getStartDate());
                        transaction.setEndDate(userSubscription.getEndDate());
                    } else {
                        transaction = mapToTransaction(userSubscription, invoiceId, transactionStatus, eventType, status, invoice);
                    }
                }
                transactionRepository.save(transaction);
            } else {
                throw new ResourceNotFoundException(String.format("UserSubscription not found with given payment provider subscription-id: %s", subscriptionId));
            }
            log.info("handlerWebhookEvents() :: Exit");
        } catch (Exception e) {
            log.error("Error occurred while handling the stripe webhook event: {}: error-message:{}", eventId, e.getMessage(), e);
        }
    }

    private static Transaction mapToTransaction(UserSubscription userSubscription, String invoiceId, TransactionStatus transactionStatus, String eventType, String status, Invoice invoice) {
        Transaction transaction;
        transaction = Transaction.builder()
                .user(userSubscription.getUser())
                .paymentMethodId(userSubscription.getPaymentMethodId())
                .paymentProviderTransactionId(invoiceId)
                .userSubscription(userSubscription)
                .status(transactionStatus)
                .webhookEventType(eventType)
                .paymentProviderSubscriptionStatus(status)
                .paymentProviderInvoiceStatus(invoice.getStatus())
                .startDate(userSubscription.getStartDate())
                .endDate(userSubscription.getEndDate())
                .build();
        return transaction;
    }

    private TransactionStatus mapToTransactionStatus(String invoiceStatus, String subscriptionStatus) {
        log.info("handlerWebhookEvents() :: mapToTransactionStatus() invoked.");
        log.info("Invoice status : {}", invoiceStatus);
        if (subscriptionStatus.equalsIgnoreCase(SUBSCRIPTION_STATUS_CANCELED)) {
            return TransactionStatus.SUCCESS;
        } else if (subscriptionStatus.equalsIgnoreCase(SUBSCRIPTION_STATUS_ACTIVE) ||
                subscriptionStatus.equalsIgnoreCase(SUBSCRIPTION_STATUS_TRIALING)) {
            if(invoiceStatus.equalsIgnoreCase(INVOICE_STATUS_PAID)){
                return TransactionStatus.SUCCESS;
            } else if(invoiceStatus.equalsIgnoreCase(INVOICE_STATUS_DRAFT) || invoiceStatus.equalsIgnoreCase(INVOICE_STATUS_OPEN)) {
                return TransactionStatus.PENDING;
            }else {
                return TransactionStatus.FAIL;
            }
        } else if(subscriptionStatus.equalsIgnoreCase(SUBSCRIPTION_STATUS_INCOMPLETE_EXPIRED)
                || subscriptionStatus.equalsIgnoreCase(SUBSCRIPTION_STATUS_UNPAID)
                || subscriptionStatus.equalsIgnoreCase(SUBSCRIPTION_STATUS_PAST_DUE)) {
            if (invoiceStatus.equalsIgnoreCase(INVOICE_STATUS_DRAFT) || invoiceStatus.equalsIgnoreCase(INVOICE_STATUS_OPEN)) {
                return TransactionStatus.FAIL;
            } else {
                return TransactionStatus.PENDING;
            }
        } else if (invoiceStatus.equalsIgnoreCase(INVOICE_STATUS_VOID) || invoiceStatus.equalsIgnoreCase(INVOICE_STATUS_UNCOLLECTIBLE)) {
            return TransactionStatus.FAIL;
        } else{
            return TransactionStatus.FAIL;
        }
    }

    private UserSubscriptionStatus mapToUserSubscriptionStatus(String status, String type) {
        log.info("handlerWebhookEvents() :: mapToUserSubscriptionStatus() invoked.");
        if(type.equalsIgnoreCase(STRIPE_WEB_HOOK_EVENT_TYPE_CUSTOMER_SUBSCRIPTION_TRIAL_WILL_END) ||
                type.equalsIgnoreCase(STRIPE_WEB_HOOK_EVENT_TYPE_CUSTOMER_SUBSCRIPTION_PENDING_UPDATE_EXPIRED)) {
            return UserSubscriptionStatus.INACTIVE;
        } else if(type.equalsIgnoreCase(STRIPE_WEB_HOOK_EVENT_TYPE_CUSTOMER_SUBSCRIPTION_DELETED)){
            return UserSubscriptionStatus.CANCELLED;
        } else if(type.equalsIgnoreCase(STRIPE_WEB_HOOK_EVENT_TYPE_CUSTOMER_SUBSCRIPTION_UPDATED) ||
                type.equalsIgnoreCase(STRIPE_WEB_HOOK_EVENT_TYPE_CUSTOMER_SUBSCRIPTION_RESUMED) ||
                type.equalsIgnoreCase(STRIPE_WEB_HOOK_EVENT_TYPE_CUSTOMER_SUBSCRIPTION_PENDING_UPDATE_APPLIED) ||
                type.equalsIgnoreCase(STRIPE_WEB_HOOK_EVENT_TYPE_CUSTOMER_SUBSCRIPTION_CREATED)) {
            if(status.equalsIgnoreCase(SUBSCRIPTION_STATUS_ACTIVE) || status.equalsIgnoreCase(SUBSCRIPTION_STATUS_TRIALING)) {
                return UserSubscriptionStatus.ACTIVE;
            } else if(status.equalsIgnoreCase(SUBSCRIPTION_STATUS_INCOMPLETE_EXPIRED)
                    || status.equalsIgnoreCase(SUBSCRIPTION_STATUS_UNPAID)
                    || status.equalsIgnoreCase(SUBSCRIPTION_STATUS_PAST_DUE)) {
                return UserSubscriptionStatus.INACTIVE;
            } else {
                return UserSubscriptionStatus.CANCELLED;
            }
        } else {
            return UserSubscriptionStatus.CANCELLED;
        }
    }
}
