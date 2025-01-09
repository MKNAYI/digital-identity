package com.proof_backend.model;

import com.proof_backend.enums.UserSubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionUpdateRequestDTO {
    private UserSubscriptionStatus status;
    private String cancelReason;
}
