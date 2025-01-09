package com.proof_backend.service;

import com.proof_backend.entity.User;
import com.proof_backend.entity.UserSubscription;
import com.proof_backend.enums.AccountType;
import com.proof_backend.enums.UserSubscriptionStatus;
import com.proof_backend.exceptions.CustomException;
import com.proof_backend.exceptions.ResourceNotFoundException;
import com.proof_backend.model.SubscriptionRequestDTO;
import com.proof_backend.model.SubscriptionUpdateRequestDTO;
import com.proof_backend.model.UserSubscriptionDTO;
import com.proof_backend.repository.TransactionRepository;
import com.proof_backend.repository.UserSubscriptionRepository;
import com.proof_backend.utils.Utils;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import static com.proof_backend.utils.Constants.*;

import java.util.List;

@Service
@Slf4j
public class PaymentProviderService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${subscription.trial-day}")
    private Long subscriptionTrialDay;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public void createPaymentProviderSubscription(Long userId, SubscriptionRequestDTO subscriptionRequest) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        String paymentMethodId = subscriptionRequest.getPaymentMethodId();
        String productId = subscriptionRequest.getProductId();

        log.info("PaymentProviderService :: createPaymentProviderSubscription() invoked. user-id: {}, product-id: {}", userId, productId);

        PaymentMethod paymentMethod = getPaymentMethod(paymentMethodId);
        log.info("PaymentMethod fetched successfully, user-id: {}", userId);

        Product product = getProduct(productId);
        log.info("Product fetched successfully, user-id: {}", userId);

        User user = userService.getUserById(userId);
        Customer customer = createCustomer(user.getName(), user.getEmail());
        String customerId = customer.getId();
        log.info("Customer created successfully, user-id: {}", userId);

        // Attach the payment method to the customer
        attachPaymentMethodToCustomer(paymentMethod, customerId);
        log.info("Attached payment method to customer successfully, user-id: {}", userId);

        String priceId = getPriceIdByProductId(productId);
        log.info("Price details fetched successfully, user-id: {}", userId);

        // Create payment provider subscription params
        Subscription subscription = createPaymentProviderSubscription(customerId, priceId, paymentMethod);

        if(subscription != null) {
            log.info("Subscription created successfully for user-id: {}", userId);
            UserSubscription userSubscription = mapToUserSubscription(user, subscription, productId, customerId, paymentMethodId);
            AccountType accountType = mapToAccountTypeUsingProductName(product.getName());
            log.info("AccountType: {}, user-id: {}", accountType, userId);
            user.setAccountType(accountType);
            userSubscriptionRepository.save(userSubscription);
            log.info("UserSubscription record persisted successfully, id: {}, user-id: {}", userSubscription.getId(), userId);
        } else {
            log.info("Error while creating subscription with given user: {}", userId);
            throw new CustomException(String.format("Error while creating subscription with given user: %s", userId), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("PaymentProviderService :: createPaymentProviderSubscription() Exit");
    }

    private static AccountType mapToAccountTypeUsingProductName(String productName) {
        log.info("PaymentProviderService :: createPaymentProviderSubscription() :: mapToAccountTypeUsingProductName() invoked.");
        log.info("Product name: {}", productName);
        return productName.equalsIgnoreCase(STRIPE_PRODUCT_NAME_BUSINESS_TYPE) ?
                AccountType.BUSINESS_TYPE :
                productName.equalsIgnoreCase(STRIPE_PRODUCT_NAME_INDIVIDUAL_TYPE) ? AccountType.INDIVIDUAL_TYPE : null;
    }

    private PaymentMethod getPaymentMethod(String paymentMethodId) throws StripeException {
        log.info("PaymentProviderService :: createPaymentProviderSubscription() :: getPaymentMethod() invoked.");
        return PaymentMethod.retrieve(paymentMethodId);
    }

    private Product getProduct(String productId) throws StripeException {
        log.info("PaymentProviderService :: createPaymentProviderSubscription() :: getProduct() invoked.");
        return Product.retrieve(productId);
    }

    private UserSubscription mapToUserSubscription(User user, Subscription subscription, String productId, String customerId, String paymentMethodId) {
        log.info("PaymentProviderService :: createPaymentProviderSubscription() :: mapToUserSubscription() invoked.");
        return UserSubscription.builder()
                .user(user)
                .status(mapToUserSubscriptionStatus(subscription.getStatus()))
                .paymentProviderProductId(productId)
                .paymentProviderSubscriptionId(subscription.getId())
                .customerId(customerId)
                .paymentMethodId(paymentMethodId)
                .startDate(Utils.toCovertLongDateToLocalDateTime(subscription.getCurrentPeriodStart()))
                .endDate(Utils.toCovertLongDateToLocalDateTime(subscription.getCurrentPeriodEnd()))
                .build();
    }

    private UserSubscriptionStatus mapToUserSubscriptionStatus(String status) {
        log.info("PaymentProviderService :: createPaymentProviderSubscription() :: mapToUserSubscriptionStatus() invoked, status : {}", status);
        if(status.equalsIgnoreCase(SUBSCRIPTION_STATUS_ACTIVE) || status.equalsIgnoreCase(SUBSCRIPTION_STATUS_TRIALING)){
            return UserSubscriptionStatus.ACTIVE;
        } else if(status.equalsIgnoreCase(SUBSCRIPTION_STATUS_INCOMPLETE_EXPIRED)
                || status.equalsIgnoreCase(SUBSCRIPTION_STATUS_UNPAID)
                || status.equalsIgnoreCase(SUBSCRIPTION_STATUS_PAST_DUE)){
            return UserSubscriptionStatus.INACTIVE;
        } else{
            return UserSubscriptionStatus.CANCELLED;
        }
    }

    private Subscription createPaymentProviderSubscription(String customerId, String priceId, PaymentMethod paymentMethod) throws StripeException {
        log.info("PaymentProviderService :: createPaymentProviderSubscription() :: createPaymentProviderSubscription() invoked");
        SubscriptionCreateParams params = SubscriptionCreateParams.builder()
                .setCustomer(customerId)
                .addItem(SubscriptionCreateParams.Item.builder()
                        .setPrice(priceId) // Use the price ID associated with your product
                        .build())
                .setDefaultPaymentMethod(paymentMethod.getId())
                .setTrialPeriodDays(subscriptionTrialDay)
                .setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
                .build();
        return Subscription.create(params);
    }

    private String getPriceIdByProductId(String productId) throws StripeException {
        log.info("PaymentProviderService :: createPaymentProviderSubscription() :: getPriceIdByProductId() invoked.");
        PriceListParams priceCreateParams = PriceListParams.builder()
                .setProduct(productId)
                .build();
        List<Price> price = Price.list(priceCreateParams).getData();
        if(CollectionUtils.isNotEmpty(price)){
            return price.get(0).getId();
        } else {
            throw new CustomException(String.format("Error while getting the price details by product id: %s", productId), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Customer createCustomer(String name, String email) throws StripeException {
        log.info("PaymentProviderService :: createPaymentProviderSubscription() :: createCustomer() invoked.");
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(name)
                .setEmail(email)
                .build();

        return Customer.create(params);
    }

    private void attachPaymentMethodToCustomer(PaymentMethod resource, String customerId) throws StripeException {
        log.info("PaymentProviderService :: attachPaymentMethodToCustomer() invoked.");
        PaymentMethodAttachParams params =
                PaymentMethodAttachParams.builder().setCustomer(customerId).build();
       resource.attach(params);
    }

    public void changePaymentProviderSubscriptionPaymentMethod(Long userId, SubscriptionRequestDTO subscriptionRequest) throws StripeException {
        log.info("PaymentProviderService :: changePaymentProviderSubscriptionPaymentMethod() invoked. user-id: {}", userId);

        Stripe.apiKey = stripeSecretKey;
        String paymentMethodId = subscriptionRequest.getPaymentMethodId();

        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        log.info("PaymentMethod fetched successfully, user-id: {}", userId);

        User user = userService.getUserById(userId);
        UserSubscription userSubscription = user.getLatestSubscription();
        String existsPaymentMethodId = userSubscription.getPaymentMethodId();

        if (userSubscription == null) {
            throw new ResourceNotFoundException(String.format("UserSubscription not found with given user-id: %s.", userId));
        } else {
            String customerId = userSubscription.getCustomerId();

            // Detach the payment method to the customer
            detachPaymentMethodToCustomer(existsPaymentMethodId);
            log.info("Existing payment method detached successfully, user-id: {}, user-subscription-id: {}", userId, userSubscription.getId());

            // Attach the payment method to the customer
            attachPaymentMethodToCustomer(paymentMethod, customerId);
            log.info("Attached payment method to customer successfully, user-id: {}", userId);

            Subscription subscription = getSubscription(userSubscription.getPaymentProviderSubscriptionId());
            log.info("subscription: {}, status: {}, invoice-id: {}", subscription.getId(), subscription.getStatus(), subscription.getLatestInvoice());
            String invoiceId = subscription.getLatestInvoice();
            if (!userSubscription.getStatus().equals(UserSubscriptionStatus.ACTIVE)) {
                Invoice invoice = getInvoice(invoiceId);
                log.info("Invoice fetched successfully, invoice-status: {}, user-id: {}", invoice.getStatus(), userId);
                if(invoice.getStatus().equals(INVOICE_STATUS_OPEN)) {
                    Invoice paidInvoice = payInvoice(paymentMethodId, invoice);
                    log.info("Invoice status: {}, invoice-id: {}", paidInvoice.getStatus(), paidInvoice.getId());
                }
            }

            changePaymentMethodIntoSubscription(paymentMethodId, subscription);
            log.info("Payment-method updated successfully into the payment-provider-subscription, user-id: {}", userId);

            log.info("Update payment-method, user-id: {}, user-subscription-status: {}", userId, userSubscription.getStatus());
            userSubscription.setPaymentMethodId(paymentMethodId);
            userSubscriptionRepository.save(userSubscription);
            log.info("Payment-method updated successfully, user-id: {}", userId);
        }
    }

    private void changePaymentMethodIntoSubscription(String paymentMethodId, Subscription subscription) throws StripeException {
        SubscriptionUpdateParams subscriptionUpdateParams =  SubscriptionUpdateParams.builder()
                .setDefaultPaymentMethod(paymentMethodId)
                .build();
        subscription.update(subscriptionUpdateParams);
    }

    private Invoice payInvoice(String paymentMethodId, Invoice invoice) throws StripeException {
        InvoicePayParams invoicePayParams = InvoicePayParams.builder()
                .setPaymentMethod(paymentMethodId)
                .build();
        return invoice.pay(invoicePayParams);
    }

    private Invoice getInvoice(String invoiceId) throws StripeException {
        return Invoice.retrieve(invoiceId);
    }

    private Subscription getSubscription(String subscriptionId) throws StripeException {
        log.info("PaymentProviderService :: getSubscription() invoked, payment-provider-subscription-id: {}", subscriptionId);
        return Subscription.retrieve(subscriptionId);
    }

    private void detachPaymentMethodToCustomer(String existsPaymentMethodId) throws StripeException {
        log.info("PaymentProviderService :: detachPaymentMethodToCustomer() invoked");
        PaymentMethod existsPaymentMethod = PaymentMethod.retrieve(existsPaymentMethodId);
        PaymentMethodDetachParams detachParams = PaymentMethodDetachParams.builder().build();
        existsPaymentMethod.detach(detachParams);
    }

    public Subscription cancelPaymentProviderSubscription(String subscriptionId) throws StripeException {
        log.info("PaymentProviderService :: cancelPaymentProviderSubscription() invoked, payment-provider-subscription-id: {}", subscriptionId);
        Stripe.apiKey = stripeSecretKey;
        Subscription resource = Subscription.retrieve(subscriptionId);
        log.info("PaymentProviderService :: cancelPaymentProviderSubscription() :: Payment provider subscription fetched successfully with payment-provider-subscription-id: {}", subscriptionId);
        SubscriptionCancelParams params = SubscriptionCancelParams.builder().build();
        return resource.cancel(params);
    }

    public UserSubscriptionDTO cancelPaymentProviderSubscription(Long userId, SubscriptionUpdateRequestDTO updateRequestDTO) throws StripeException, BadRequestException {
        log.info("PaymentProviderService :: cancelPaymentProviderSubscription() invoked, user-id: {}", userId);
        User user = userService.getUserById(userId);
        UserSubscription userSubscription = user.getActiveSubscription();
        if(userSubscription == null) {
            throw new ResourceNotFoundException(String.format("UserSubscription not found with given user-id: %s.", userId));
        }else {
            log.info("User subscription status: {}, user-id: {}", updateRequestDTO.getStatus(), userId);
            Long subscriptionId = userSubscription.getId();
            if (updateRequestDTO.getStatus().equals(UserSubscriptionStatus.CANCELLED)) {
                userSubscription.setStatus(UserSubscriptionStatus.CANCELLED);
                userSubscription.setCancellationReason(updateRequestDTO.getCancelReason());

                // Cancel stripe subscription
                cancelPaymentProviderSubscription(userSubscription.getPaymentProviderSubscriptionId());
                log.info("Payment provider subscription cancel successfully, user-id: {}, user-subscription-id: {}", userId, subscriptionId);

                userSubscriptionRepository.save(userSubscription);
                log.info("User subscription cancel successfully, user-id: {}, user-subscription-id: {}", userId, subscriptionId);
            } else {
               throw new BadRequestException("Required input status as CANCELLED");
            }
            return UserSubscription.toDto(userSubscription);
        }
    }
}
