package com.uniskills.main.controller;

import com.uniskills.main.dto.UserDto;
import com.uniskills.main.model.User;
import com.uniskills.main.repository.UserRepository;
import com.uniskills.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")

public class UserController {

    @Autowired
    private UserRepository userRepository;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ✅ 1. Get User Profile (By ID)
    // हा endpoint डॅशबोर्डवर आणि मार्केटप्लेसवर दुसऱ्यांचे प्रोफाइल बघण्यासाठी वापरला जाईल
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable Long userId) {
        UserDto userDto = userService.getUserProfile(userId);
        return ResponseEntity.ok(userDto);
    }

    // ✅ 2. Update Profile
    // जेव्हा युजर "Edit Profile" करून Save करेल, तेव्हा हा कॉल होईल
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateProfile(@PathVariable Long userId, @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateProfile(userId, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    // ✅ UPLOAD PROFILE IMAGE
    @PostMapping("/{id}/image")
    public ResponseEntity<?> uploadProfileImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 1. फोल्डर बनवा (जर नसेल तर)
            String uploadDir = "uploads/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. फाईलचे युनिक नाव तयार करा (image.jpg -> user_123_uuid.jpg)
            String fileName = "user_" + id + "_" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // 3. फाईल सेव्ह करा
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 4. डेटाबेसमध्ये URL सेव्ह करा
            // आपण '/images/' प्रीफिक्स वापरतोय जो WebConfig मध्ये सेट केलाय
            String fileUrl = "http://localhost:8080/images/" + fileName;
            user.setProfileImage(fileUrl);
            userRepository.save(user);

            return ResponseEntity.ok(user);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload image");
        }
    }
}