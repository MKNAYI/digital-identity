package com.proof_backend.service;

import com.proof_backend.entity.User;
import com.proof_backend.entity.UserSubscription;
import com.proof_backend.entity.UserVerificationLink;
import com.proof_backend.enums.UserStatus;
import com.proof_backend.enums.UserSubscriptionStatus;
import com.proof_backend.exceptions.CustomException;
import com.proof_backend.exceptions.ResourceNotFoundException;
import com.proof_backend.model.UserDTO;
import com.proof_backend.model.UserVerificationLinkDTO;
import com.proof_backend.repository.UserVerificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Base64;

@Service
@Slf4j
public class UserVerificationLinkService {

    @Value("${user.verification-link-url}")
    private String userVerificationLinkURL;

    @Value("${user.verification-secret-key}")
    private String secretKey;

    @Autowired
    private UserVerificationRepository verificationRepository;

    @Autowired
    private UserService userService;

    public UserVerificationLinkDTO getUserVerificationLink(Long userId) {
        log.info("UserVerificationLinkService :: getUserVerificationLink() invoked, user-id: {}", userId);
        UserVerificationLink userVerificationLink = verificationRepository.findByUserId(userId);
        if (userVerificationLink != null) {
            return UserVerificationLink.toDto(userVerificationLink);
        } else {
            throw new ResourceNotFoundException(String.format("UserVerification not found with given user-id: %s", userId));
        }
    }

    public UserDTO verifyUserLink(String encodedUserId) {
        log.info("UserVerificationLinkService :: userVerify() invoked, encoded-user-id: {}", encodedUserId);
        Long decodedUserId = decodeUserId(encodedUserId, secretKey);
        User user = userService.getUserById(decodedUserId);
        log.info("User fetched successfully. user-id: {}", decodedUserId);
        UserSubscription userSubscription = user.getLatestSubscription();
        if(userSubscription == null) {
            log.info("UserSubscription not found with user-id: {}", decodedUserId);
            throw new ResourceNotFoundException(String.format("UserSubscription not found with user-id: %s.", decodedUserId));
        }
        log.info("User subscription fetched successfully. user-subscription-id: {}, user-id: {}", userSubscription.getId(), decodedUserId);
        preCheckValidation(user, userSubscription);
        return User.toDto(user);
    }

    public UserVerificationLinkDTO createUserVerificationLink(Long userId) {
        log.info("UserVerificationLinkService :: createUserVerificationLink() invoked");
        User user = userService.getUserById(userId);
        log.info("User fetched successfully. user-id: {}", userId);
        UserSubscription userSubscription = user.getLatestSubscription();
        if(userSubscription == null) {
            log.info("UserSubscription not found with user-id: {}", userId);
            throw new ResourceNotFoundException(String.format("UserSubscription not found with user-id: %s.", userId));
        }
        log.info("User subscription fetched successfully. user-subscription-id: {}", userSubscription.getId());
        preCheckValidation(user, userSubscription);

        UserVerificationLink userVerificationLink = verificationRepository.findByUserId(userId);
        if(userVerificationLink == null) {
            String encodedUserId = encodeUserId(userId, secretKey);

            String publicLinkURL = MessageFormat.format(userVerificationLinkURL, encodedUserId);
            String qrLinkURL = MessageFormat.format(userVerificationLinkURL, encodedUserId);

            userVerificationLink = UserVerificationLink.builder()
                    .publicLinkUrl(publicLinkURL)
                    .qrLinkUrl(qrLinkURL)
                    .user(user)
                    .build();
        } else {
//            userVerificationLink.setPublicLinkUrl(publicLinkURL);
//            userVerificationLink.setQrLinkUrl(qrLinkURL);
        }
        verificationRepository.save(userVerificationLink);
        return UserVerificationLink.toDto(userVerificationLink);
    }

    private static void preCheckValidation(User user, UserSubscription userSubscription) {
        log.info("UserVerificationLinkService :: preCheckValidation() invoked, user-status: {}, document-expiration-date: {}, userSubscription-status: {}, user-id: {}", user.getStatus(), user.getExpirationDate(), userSubscription.getStatus(), user.getId());

        if(!user.getStatus().equals(UserStatus.VERIFIED)) {
            throw new CustomException("User is not verified.", HttpStatus.BAD_REQUEST);
        }

        if(!userSubscription.getStatus().equals(UserSubscriptionStatus.ACTIVE)) {
            throw new CustomException("User subscription is not active.", HttpStatus.BAD_REQUEST);
        }

        if(user.getExpirationDate().isBefore(LocalDate.now())) {
            throw new CustomException("User documents are expired.", HttpStatus.BAD_REQUEST);
        }
    }

    // Method to encode a Long value using a key
    public static String encodeUserId(Long value, String key) {
        try {
            byte[] valueBytes = longToBytes(value);
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] encodedBytes = new byte[valueBytes.length];

            for (int i = 0; i < valueBytes.length; i++) {
                encodedBytes[i] = (byte) (valueBytes[i] ^ keyBytes[i % keyBytes.length]);
            }

            // Convert to a URL-safe Base64 string
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encodedBytes);
        } catch (Exception e) {
            log.error("Error occurred while encoding userId : error-message: {}", e.getMessage(), e);
            throw new CustomException(String.format("Error encoding userId: error-message: %s", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    // Method to decode a Long value using a key
    public static Long decodeUserId(String encodedValue, String key) {
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedValue);
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] valueBytes = new byte[decodedBytes.length];

            for (int i = 0; i < decodedBytes.length; i++) {
                valueBytes[i] = (byte) (decodedBytes[i] ^ keyBytes[i % keyBytes.length]);
            }

            return bytesToLong(valueBytes);
        } catch (Exception e) {
            log.error("Error occurred while decoding userId : error-message: {}", e.getMessage(), e);
            throw new CustomException(String.format("Error decoding userId: error-message: %s", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    // Helper method to convert Long to byte array
    private static byte[] longToBytes(Long value) {
        return ByteBuffer.allocate(Long.BYTES).putLong(value).array();
    }

    // Helper method to convert byte array to Long
    private static Long bytesToLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }
}
