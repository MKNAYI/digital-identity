package com.proof_backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.proof_backend.entity.User;
import com.proof_backend.enums.*;
import com.proof_backend.utils.Utils;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String image;
    private UserStatus status;
    private String country;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate issuedDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String verifiedOn;
    private AccountType accountType;
    private UserSubscriptionDTO userSubscription;
    private UserPreferenceDTO userPreference;
    private UserSocialLinkDTO userSocialLink;
}
