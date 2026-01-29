package com.uniskills.main.controller;

import com.uniskills.main.config.JwtUtils;
import com.uniskills.main.model.User;
import com.uniskills.main.repository.UserRepository;
import com.uniskills.main.service.EmailService; // 🔥 New Import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService; // 🔥 Used EmailService

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // --- 1. REGISTER ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(user.getRole() != null ? user.getRole() : "LEARNER");
            user.setEnabled(false);

            // OTP Generation
            String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
            user.setOtp(otp);
            user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));

            userRepository.save(user);

            // Send Email using Service
            emailService.sendEmail(user.getEmail(),
                    "EduChain Verification OTP",
                    "Hello,\n\nYour OTP for registration is: " + otp + "\n\nValid for 10 minutes.");

            return ResponseEntity.ok("User registered successfully! Verify your email.");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // --- 2. VERIFY OTP (For Registration) ---
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");

        User user = userOpt.get();

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("OTP expired.");
        }

        if (user.getOtp().equals(otp)) {
            user.setEnabled(true);
            user.setOtp(null);
            user.setOtpExpiry(null);
            userRepository.save(user);
            return ResponseEntity.ok("Verified! You can now login.");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }
    }

    // --- 3. LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());

        if (userOpt.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(401).body("Invalid Email or Password");
        }

        User user = userOpt.get();

        if (!user.isEnabled()) {
            return ResponseEntity.status(403).body("Email not verified.");
        }

        String token = jwtUtils.generateToken(user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());

        // 🔥🔥🔥 NEW: Send Profile Image 🔥🔥🔥
        response.put("profileImage", user.getProfileImage());

        return ResponseEntity.ok(response);
    }

    // 🔥🔥🔥 4. FORGOT PASSWORD (Send OTP) 🔥🔥🔥
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User with this email does not exist.");
        }

        User user = userOpt.get();

        // Generate OTP
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        // Send Email
        emailService.sendEmail(email,
                "Reset Password OTP - EduChain",
                "Hello " + user.getFirstName() + ",\n\nYour OTP to reset password is: " + otp + "\n\nDo not share this with anyone.");

        return ResponseEntity.ok("OTP sent to your email.");
    }

    // 🔥🔥🔥 5. RESET PASSWORD (Verify OTP & Update Password) 🔥🔥🔥
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");

        User user = userOpt.get();

        // Validate OTP
        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("OTP Expired");
        }

        // Update Password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setOtp(null);       // Clear OTP
        user.setOtpExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok("Password changed successfully! Login with new password.");
    }
}