package com.proof_backend.controller;

import com.proof_backend.model.UserSocialLinkDTO;
import com.proof_backend.service.UserSocialLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
public class UserSocialLinkController {

    @Autowired
    private UserSocialLinkService userSocialLinkService;

    @GetMapping("/users/{user-id}/social-links")
    public ResponseEntity<UserSocialLinkDTO> getUserSocialLinks(@PathVariable("user-id") Long userId) {
        return new ResponseEntity<UserSocialLinkDTO>(userSocialLinkService.getUserSocialLinks(userId), HttpStatus.OK);
    }

    @GetMapping("/users/{user-id}/social-links/{user-social-link-id}")
    public ResponseEntity<UserSocialLinkDTO> getUserSocialLinkById(@PathVariable("user-id") Long userId, @PathVariable("user-social-link-id") Long userSocialLinkId) {
        return new ResponseEntity<UserSocialLinkDTO>(userSocialLinkService.getUserSocialLinkByUserIdAndId(userId, userSocialLinkId), HttpStatus.OK);
    }

    @PostMapping("/users/{user-id}/social-links")
    public ResponseEntity<UserSocialLinkDTO> createOrUpdateUserSocialLink(@PathVariable("user-id") Long userId, @RequestBody UserSocialLinkDTO userSubscriptionDTO) {
        return new ResponseEntity<UserSocialLinkDTO>(userSocialLinkService.createOrUpdateUserSocialLink(userId, userSubscriptionDTO), HttpStatus.OK);
    }
}
