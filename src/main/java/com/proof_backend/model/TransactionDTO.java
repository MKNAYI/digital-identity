package com.proof_backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.proof_backend.entity.Auditable;
import com.proof_backend.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private UserDTO user;
    private UserSubscriptionDTO userSubscription;
    private String paymentMethodId;
    private TransactionStatus status;
    private String paymentProviderTransactionId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
}
