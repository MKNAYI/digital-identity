package com.proof_backend.repository;

import com.proof_backend.entity.Transaction;
import com.proof_backend.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(value = "select * from transaction t where t.user_subscription_id=?1 order by id desc limit 1",nativeQuery = true)
    Transaction findTopByUserSubscriptionId(Long userSubscriptionId);
}
