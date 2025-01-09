package com.proof_backend.model;

import com.proof_backend.entity.UserSocialLink;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSocialLinkDTO {
    private Long id;
    private Long userId;
    private String facebookUrl;
    private String instagramUrl;
    private String twitterUrl;
    private String tiktokUrl;
    private String snapchatUrl;
    private String linkedinUrl;

    public static UserSocialLink toEntity(UserSocialLinkDTO userSocialLinkDTO) {
        return UserSocialLink.builder()
                .facebookUrl(userSocialLinkDTO.getFacebookUrl())
                .instagramUrl(userSocialLinkDTO.getInstagramUrl())
                .twitterUrl(userSocialLinkDTO.getTwitterUrl())
                .tiktokUrl(userSocialLinkDTO.getTiktokUrl())
                .snapchatUrl(userSocialLinkDTO.getSnapchatUrl())
                .linkedinUrl(userSocialLinkDTO.getLinkedinUrl())
                .build();
    }
}
