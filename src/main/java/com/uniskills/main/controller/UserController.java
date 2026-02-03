package com.uniskills.main.controller;

import com.uniskills.main.dto.UserDto;
import com.uniskills.main.model.User;
import com.uniskills.main.repository.UserRepository;
import com.uniskills.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable Long userId) {
        UserDto userDto = userService.getUserProfile(userId);
        return ResponseEntity.ok(userDto);
    }


    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateProfile(@PathVariable Long userId, @RequestBody UserDto userDto) {


        UserDto updatedUserDto = userService.updateProfile(userId, userDto);


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDto.getProfileImage() != null && !userDto.getProfileImage().isEmpty()) {
            user.setProfileImage(userDto.getProfileImage()); // Direct Base64 String
            userRepository.save(user);
            updatedUserDto.setProfileImage(user.getProfileImage());
        }

        return ResponseEntity.ok(updatedUserDto);
    }


}