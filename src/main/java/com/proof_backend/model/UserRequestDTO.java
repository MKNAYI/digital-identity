package com.proof_backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.proof_backend.entity.User;
import com.proof_backend.enums.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    private String name;
    private String email;
    private Platform platform;
    private String image;

    public static User toEntity(UserRequestDTO userRequestDTO) {
        return User.builder()
                .name(userRequestDTO.getName())
                .email(userRequestDTO.getEmail())
                .platform(userRequestDTO.getPlatform())
                .image(userRequestDTO.getImage())
                .status(UserStatus.VERIFICATION_PENDING)
                .build();
    }
}
