package com.proof_backend.controller;

import com.proof_backend.model.UserDTO;
import com.proof_backend.model.UserVerificationLinkDTO;
import com.proof_backend.service.UserVerificationLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserVerificationLinkController {

    @Autowired
    private UserVerificationLinkService userVerificationLinkService;

    @GetMapping("/users/verify")
    public ResponseEntity<UserDTO> verifyUserLink(@RequestParam("encoded-user-id") String encodedUserId) {
        return new ResponseEntity<UserDTO>(userVerificationLinkService.verifyUserLink(encodedUserId),HttpStatus.OK);
    }

    @GetMapping("/users/{user-id}/verification-links")
    public ResponseEntity<UserVerificationLinkDTO> getUserVerificationLink(@PathVariable("user-id") Long userId) {
        return new ResponseEntity<UserVerificationLinkDTO>(userVerificationLinkService.getUserVerificationLink(userId),HttpStatus.OK);
    }

    @PostMapping("/users/{user-id}/verification-links")
    public ResponseEntity<UserVerificationLinkDTO> createUserVerificationLink(@PathVariable("user-id")Long userId) {
        return new ResponseEntity<UserVerificationLinkDTO>(userVerificationLinkService.createUserVerificationLink(userId), HttpStatus.CREATED);
    }
}
