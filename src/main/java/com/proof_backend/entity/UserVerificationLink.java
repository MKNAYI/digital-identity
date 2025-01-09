package com.proof_backend.entity;

import com.proof_backend.model.UserVerificationLinkDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "user_verification_link")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVerificationLink extends Auditable {
    @ManyToOne
    private User user;
    private String publicLinkUrl;
    private String qrLinkUrl;

    public static UserVerificationLinkDTO toDto(UserVerificationLink userVerificationLink) {
        return UserVerificationLinkDTO.builder()
                .id(userVerificationLink.getId())
                .userId(userVerificationLink.getUser().getId())
                .publicLinkUrl(userVerificationLink.getPublicLinkUrl())
                .qrLinkUrl(userVerificationLink.getQrLinkUrl())
                .build();
    }
}
