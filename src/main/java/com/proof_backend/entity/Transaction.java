package com.proof_backend.entity;

import com.proof_backend.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction extends Auditable {
    @ManyToOne
    private User user;
    @ManyToOne
    private UserSubscription userSubscription;
    private String paymentMethodId;
    private String paymentProviderTransactionId;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    private String webhookEventType;
    private String paymentProviderSubscriptionStatus;
    private String paymentProviderInvoiceStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
