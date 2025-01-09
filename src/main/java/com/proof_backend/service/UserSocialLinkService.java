package com.proof_backend.service;

import com.proof_backend.entity.User;
import com.proof_backend.entity.UserSocialLink;
import com.proof_backend.exceptions.ResourceNotFoundException;
import com.proof_backend.model.UserSocialLinkDTO;
import com.proof_backend.repository.UserSocialLinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserSocialLinkService {

    @Autowired
    private UserSocialLinkRepository userSocialLinkRepository;

    @Autowired
    private UserService userService;

    public UserSocialLinkDTO getUserSocialLinks(Long userId) {
        log.info("UserSocialLinkService :: getUserSocialLinks() invoked, user-id: {}", userId);
        UserSocialLink userSocialLinks = userSocialLinkRepository.findByUserId(userId);
        if (userSocialLinks != null){
            return UserSocialLink.toDto(userSocialLinks);
        } else{
            throw new ResourceNotFoundException(String.format("UserSocialLink not found with given user-id: %s", userId));
        }
    }

    public UserSocialLinkDTO getUserSocialLinkByUserIdAndId(Long userId, Long userSocialLinkId) {
        log.info("UserSocialLinkService :: getUserSocialLinkByUserIdAndId() invoked, user-id: {}", userId);
        UserSocialLink userSocialLink = userSocialLinkRepository.findByUserIdAndId(userId, userSocialLinkId);
        if (userSocialLink != null){
            return UserSocialLink.toDto(userSocialLink);
        } else{
            throw new ResourceNotFoundException(String.format("UserSocialLink not found with given user-id: %s and user-social-link-id: %s", userId, userSocialLinkId));
        }
    }

    public UserSocialLinkDTO createOrUpdateUserSocialLink(Long userId, UserSocialLinkDTO userSocialLinkDTO) {
        log.info("UserSocialLinkService :: createOrUpdateUserSocialLink() invoked, user-id: {}", userId);
        User user = userService.getUserById(userId);
        UserSocialLink userSocialLink = userSocialLinkRepository.findByUserId(userId);
        if (userSocialLink == null) {
            log.info("UserSocialLink not found with given user-id: {}, start process to add a new record", userId);
            userSocialLink = UserSocialLinkDTO.toEntity(userSocialLinkDTO);
            userSocialLink.setUser(user);
        } else {
            log.info("UserSocialLink found with given user-id: {}", userId);
            userSocialLink.setFacebookUrl(StringUtils.isNotEmpty(userSocialLinkDTO.getFacebookUrl()) ? userSocialLinkDTO.getFacebookUrl() : userSocialLink.getFacebookUrl());
            userSocialLink.setInstagramUrl(StringUtils.isNotEmpty(userSocialLinkDTO.getInstagramUrl()) ? userSocialLinkDTO.getInstagramUrl() : userSocialLink.getInstagramUrl());
            userSocialLink.setTwitterUrl(StringUtils.isNotEmpty(userSocialLinkDTO.getTwitterUrl()) ? userSocialLinkDTO.getTwitterUrl() : userSocialLink.getTwitterUrl());
            userSocialLink.setTiktokUrl(StringUtils.isNotEmpty(userSocialLinkDTO.getTiktokUrl()) ? userSocialLinkDTO.getTiktokUrl() : userSocialLink.getTiktokUrl());
            userSocialLink.setSnapchatUrl(StringUtils.isNotEmpty(userSocialLinkDTO.getSnapchatUrl()) ? userSocialLinkDTO.getSnapchatUrl() : userSocialLink.getSnapchatUrl());
            userSocialLink.setLinkedinUrl(StringUtils.isNotEmpty(userSocialLinkDTO.getLinkedinUrl()) ? userSocialLinkDTO.getLinkedinUrl() : userSocialLink.getLinkedinUrl());
        }
        userSocialLinkRepository.save(userSocialLink);
        log.info("UserSocialLink updated successfully, user-id: {}", userId);
        return UserSocialLink.toDto(userSocialLink);
    }
}
