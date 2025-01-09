package com.proof_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVerificationLinkDTO {
    private Long id;
    private Long userId;
    private String publicLinkUrl;
    private String qrLinkUrl;
}
