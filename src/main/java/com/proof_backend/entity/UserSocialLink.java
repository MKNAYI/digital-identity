package com.proof_backend.entity;

import com.proof_backend.model.UserSocialLinkDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "user_social_link")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSocialLink extends Auditable {
    @ManyToOne
    private User user;
    private String facebookUrl;
    private String instagramUrl;
    private String twitterUrl;
    private String tiktokUrl;
    private String snapchatUrl;
    private String linkedinUrl;

    public static UserSocialLinkDTO toDto(UserSocialLink userSocialLink) {
        if(userSocialLink == null) {
            return null;
        }
        return UserSocialLinkDTO.builder()
                .id(userSocialLink.getId())
                .userId(userSocialLink.getUser().getId())
                .facebookUrl(userSocialLink.getFacebookUrl())
                .instagramUrl(userSocialLink.getInstagramUrl())
                .twitterUrl(userSocialLink.getTwitterUrl())
                .tiktokUrl(userSocialLink.getTiktokUrl())
                .snapchatUrl(userSocialLink.getSnapchatUrl())
                .linkedinUrl(userSocialLink.getLinkedinUrl())
                .build();
    }
}
