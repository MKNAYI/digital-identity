package com.proof_backend.model;

import com.proof_backend.entity.UserPreference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceDTO {
    private Long id;
    private Long userId;
    private Boolean displayPersonal;
    private Boolean displaySocial;
}
