package com.proof_backend.entity;

import com.proof_backend.model.UserPreferenceDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "user_preference")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference extends Auditable {
    @OneToOne
    private User user;
    private Boolean displayPersonal;
    private Boolean displaySocial;

    public static UserPreferenceDTO toDto(UserPreference userPreference) {
        if (userPreference == null) {
            return null;
        }
        return UserPreferenceDTO.builder()
                .id(userPreference.getId())
                .userId(userPreference.getUser().getId())
                .displayPersonal(userPreference.getDisplayPersonal())
                .displaySocial(userPreference.getDisplaySocial())
                .build();
    }

}
