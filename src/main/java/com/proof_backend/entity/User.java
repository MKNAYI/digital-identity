package com.proof_backend.entity;

import com.proof_backend.enums.*;
import com.proof_backend.model.*;
import com.proof_backend.utils.Utils;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends Auditable {

    private String name;

    private String email;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    private String image;

    private String fullName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String nationality;

    private String state;

    private String country;

    private LocalDate issuedDate;

    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private LocalDate birthDate;

    private LocalDateTime verifiedOn;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserSubscription> subscriptions;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserPreference userPreference;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserSocialLink userSocialLink;

    public UserSubscription getLatestSubscription() {
        if(CollectionUtils.isEmpty(subscriptions)) {
            return null;
        }
        return subscriptions.stream()
                 .filter(Objects::nonNull)
                .sorted((sub1, sub2) -> sub2.getCreatedAt().compareTo(sub1.getCreatedAt()))
                .findFirst()
                .orElse(null);
    }

    public UserSubscription getActiveSubscription() {
        if(CollectionUtils.isEmpty(subscriptions)) {
            return null;
        }
        return subscriptions.stream()
                .filter(Objects::nonNull)
                .filter(subscription -> subscription.getStatus().equals(UserSubscriptionStatus.ACTIVE))
                .sorted((sub1, sub2) -> sub2.getCreatedAt().compareTo(sub1.getCreatedAt()))
                .findFirst()
                .orElse(null);
    }

    public static UserDTO toDto(User user) {
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .image(user.getImage())
                .status(user.getStatus())
                .userPreference(UserPreference.toDto(user.getUserPreference()))
                .userSubscription(UserSubscription.toDto(user.getLatestSubscription()))
                .accountType(user.getAccountType())
                .build();

        if(user.getStatus().equals(UserStatus.VERIFIED)) {
            if(user.getUserPreference() != null) {
                if (user.getUserPreference().getDisplayPersonal() == Boolean.TRUE) {
                    userDTO.setBirthDate(user.getBirthDate());
                    userDTO.setIssuedDate(user.getIssuedDate());
                    userDTO.setCountry(user.getCountry());
                    userDTO.setExpirationDate(user.getExpirationDate());
                    userDTO.setVerifiedOn(Utils.convertLocalDateTimeToEST(LocalDateTime.now()));
                } else {
                    userDTO.setVerifiedOn(Utils.convertLocalDateTimeToEST(LocalDateTime.now()));
                }

                if (user.getUserPreference().getDisplaySocial() == Boolean.TRUE) {
                    userDTO.setUserSocialLink(UserSocialLink.toDto(user.getUserSocialLink()));
                }
            }
        }
        return userDTO;
    }

}
