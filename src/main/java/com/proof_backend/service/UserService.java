package com.proof_backend.service;

import com.proof_backend.entity.User;
import com.proof_backend.entity.UserPreference;
import com.proof_backend.entity.UserSocialLink;
import com.proof_backend.enums.UserStatus;
import com.proof_backend.exceptions.CustomException;
import com.proof_backend.exceptions.ResourceNotFoundException;
import com.proof_backend.model.UserDTO;
import com.proof_backend.model.UserPreferenceDTO;
import com.proof_backend.model.UserRequestDTO;
import com.proof_backend.model.idanalyzer.DocumentScanDTO;
import com.proof_backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long userId) {
        log.info("UserService :: getUserById() invoked, user-id: {}", userId);
        return  userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(String.format("User not found with given user-id: %s", userId)));
    }

    public UserDTO getUserByEmail(String email) {
        log.info("UserService :: getUserByEmail() invoked, email: {}", email);
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // if status is verified and documents are expired then change status to EXPIRED
            if (user.getStatus().equals(UserStatus.VERIFIED) && user.getExpirationDate().isBefore(LocalDate.now())){
                user.setStatus(UserStatus.EXPIRED);
                log.info("User document has expired : expiry: {}, user-id: {}", user.getExpirationDate(), user.getId());
                userRepository.save(user);
                log.info("User status updated expired with given user-id: {}", user.getId());
            }

            log.info("User fetched successfully, User ID : {}", user.getId());
            return User.toDto(user);
        } else {
            log.error("User not found with email : {}", email);
            throw new ResourceNotFoundException(String.format("User not found with given email: %s. ", email), "Please sign up.");
        }
    }

    public UserDTO createNewUser(UserRequestDTO userRequestDTO) {
        log.info("UserService :: createNewUser() invoked...");
        Optional<User> userOptional = userRepository.findByEmail(userRequestDTO.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            log.info("User fetched successfully while saving user, user-id : {}, Platform : {}", user.getId(), user.getPlatform());
            throw new CustomException(String.format("User already exists with platform %s.", user.getPlatform()), "Please continue with sign in", HttpStatus.CONFLICT);
        } else {
            User user = UserRequestDTO.toEntity(userRequestDTO);
            UserPreference userPreference = UserPreference.builder()
                    .displayPersonal(Boolean.TRUE)
                    .displaySocial(Boolean.TRUE)
                    .user(user)
                    .build();
            UserSocialLink userSocialLink = UserSocialLink.builder()
                    .user(user)
                    .build();
            user.setUserPreference(userPreference);
            user.setUserSocialLink(userSocialLink);
            userRepository.save(user);
            log.info("User saved successfully, User ID : {}", user.getId());
            return User.toDto(user);
        }
    }

    public void updateUserVerificationData(Long userId, DocumentScanDTO documentScanDTO) {
        log.info("UserService :: updateUserVerificationData() invoked, user-id: {}", userId);
        User user = getUserById(userId);
        User updatedUser = mapToUser(user, documentScanDTO);
        userRepository.save(updatedUser);
        log.info("User updated successfully: user-id: {}", userId);
    }

    private User mapToUser(User user, DocumentScanDTO documentScanDTO) {
        log.info("UserService :: mapToUser() invoked, user-id: {}", user.getId());
        user.setStatus(UserStatus.VERIFIED);
        user.setBirthDate(documentScanDTO.getBirthDate());
        user.setDocumentType(documentScanDTO.getDocumentType());
        user.setIssuedDate(documentScanDTO.getIssuedDate());
        user.setExpirationDate(documentScanDTO.getExpirationDate());
        user.setGender(documentScanDTO.getGender());
        user.setFullName(documentScanDTO.getFullName());
        user.setNationality(documentScanDTO.getNationality());
        user.setVerifiedOn(LocalDateTime.now());
        user.setCountry(documentScanDTO.getCountry());
        user.setState(documentScanDTO.getState());
        return user;
    }

    public void updateUserStatus(Long userId, UserStatus status) {
        log.info("UserService :: updateUserStatus() invoked, user-id: {}", userId);
        User user = getUserById(userId);
        user.setStatus(status);
        user.setVerifiedOn(LocalDateTime.now());
        userRepository.save(user);
        log.info("User status updated successfully: user-id: {}", userId);
        log.info("UserService :: updateUserStatus() Exit");
    }

    public void updateUserPreference(Long userId, UserPreferenceDTO preference) {
        log.info("UserService :: updateUserPreference() invoked, user-id: {}, preference: {}", userId, preference);
        User user = getUserById(userId);
        UserPreference userPreference = user.getUserPreference();
        if(userPreference != null) {
            userPreference.setDisplayPersonal(preference.getDisplayPersonal() != null ? preference.getDisplayPersonal() : userPreference.getDisplayPersonal());
            userPreference.setDisplaySocial(preference.getDisplaySocial() != null ? preference.getDisplaySocial() : userPreference.getDisplaySocial());
        } else {
            log.error("UserPreference not found with given user-id: {}", userId);
            throw new ResourceNotFoundException(String.format("UserPreference not found with given user-id: %s", userId));
        }
        userRepository.save(user);
        log.info("User preference updated successfully: user-id: {}", userId);
        log.info("UserService :: updateUserPreference() Exit");
    }

    public void updateUserProfile(Long userId, UserRequestDTO userRequestDTO) {
        log.info("UserService :: updateUserProfile() invoked, user-id: {}, ", userId);
        User user = getUserById(userId);
        user.setImage(StringUtils.isNotEmpty(userRequestDTO.getImage()) ? userRequestDTO.getImage() : user.getImage());
        user.setName(StringUtils.isNotEmpty(userRequestDTO.getName()) ? userRequestDTO.getName() : user.getName());
        userRepository.save(user);
        log.info("User profile updated successfully: user-id: {}", userId);
        log.info("UserService :: updateUserProfile() Exit");
    }
}
