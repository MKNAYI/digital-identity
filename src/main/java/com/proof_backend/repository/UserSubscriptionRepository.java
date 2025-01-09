package com.proof_backend.repository;

import com.proof_backend.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    UserSubscription findByUserId(Long userId);

    UserSubscription findByPaymentProviderSubscriptionId(String subscriptionId);

    List<UserSubscription> findAllByUserId(Long userId);

    UserSubscription findByUserIdAndId(Long userId, Long subscriptionId);
}
