package com.proof_backend.repository;

import com.proof_backend.entity.UserVerificationLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerificationLink, Long> {
    UserVerificationLink findByUserId(Long userId);
}
