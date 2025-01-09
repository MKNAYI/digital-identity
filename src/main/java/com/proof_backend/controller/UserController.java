package com.proof_backend.controller;

import com.proof_backend.model.UserDTO;
import com.proof_backend.model.UserPreferenceDTO;
import com.proof_backend.model.UserRequestDTO;
import com.proof_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
        return new ResponseEntity<UserDTO>(userService.getUserByEmail(email), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<UserDTO> createNewUser(@RequestBody UserRequestDTO userRequestDTO) {
        return new ResponseEntity<UserDTO>(userService.createNewUser(userRequestDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/{user-id}/preference")
    public ResponseEntity<?> updateUserPreference(@PathVariable("user-id") Long userId, @RequestBody UserPreferenceDTO preference) {
        userService.updateUserPreference(userId, preference);
        return new ResponseEntity<UserDTO>(HttpStatus.OK);
    }

    @PatchMapping("/{user-id}/profile")
    public ResponseEntity<?> updateUserProfile(@PathVariable("user-id") Long userId, @RequestBody UserRequestDTO userRequestDTO) {
        userService.updateUserProfile(userId, userRequestDTO);
        return new ResponseEntity<UserDTO>(HttpStatus.OK);
    }
}
