package com.proof_backend.entity;

import com.proof_backend.enums.UserSubscriptionStatus;
import com.proof_backend.model.UserSubscriptionDTO;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "user_subscription")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscription extends Auditable {
    @ManyToOne
    private User user;
    private String paymentProviderSubscriptionId;
    private String paymentProviderProductId;
    @Enumerated(EnumType.STRING)
    private UserSubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String customerId;
    private String paymentMethodId;
    private String cancellationReason;

    public static UserSubscriptionDTO toDto(UserSubscription userSubscription) {
        if (userSubscription == null) {
            return null;
        }
        return UserSubscriptionDTO
                .builder()
                .id(userSubscription.getId())
                .userId(userSubscription.getUser().getId())
                .status(userSubscription.getStatus())
                .startDate(userSubscription.getStartDate())
                .endDate(userSubscription.getEndDate())
                .build();
    }
}
