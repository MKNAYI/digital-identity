package com.proof_backend.repository;

import com.proof_backend.entity.UserSocialLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSocialLinkRepository extends JpaRepository<UserSocialLink, Long> {
    UserSocialLink findByUserId(Long userId);

    UserSocialLink findByUserIdAndId(Long userId, Long userSubscriptionId);
}
