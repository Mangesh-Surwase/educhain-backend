package com.uniskills.main.controller;

import com.uniskills.main.config.JwtUtils;
import com.uniskills.main.model.User;
import com.uniskills.main.repository.UserRepository;
import com.uniskills.main.service.EmailService;
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
    private EmailService emailService;

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

            String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
            user.setOtp(otp);
            user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));

            // CRITICAL FIX: Use saveAndFlush to force DB commit immediately
            userRepository.saveAndFlush(user);

            System.out.println("USER REGISTERED: " + user.getEmail() + " | OTP: " + otp);

            // CRITICAL FIX: Send email in a separate thread so it does not block or rollback registration
            new Thread(() -> {
                emailService.sendEmail(user.getEmail(), "EduChain Verification OTP", "Your OTP is: " + otp);
            }).start();

            return ResponseEntity.ok("User registered successfully. Check email or logs for OTP.");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // --- 2. VERIFY OTP ---
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");

        User user = userOpt.get();

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("OTP expired.");
        }

        if (user.getOtp().trim().equals(otp.trim())) {
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
        response.put("profileImage", user.getProfileImage());

        return ResponseEntity.ok(response);
    }

    // --- 4. FORGOT PASSWORD ---
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOpt.get();
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        System.out.println("RESET OTP: " + otp);

        new Thread(() -> {
            emailService.sendEmail(email, "Reset Password OTP", "Your OTP is: " + otp);
        }).start();

        return ResponseEntity.ok("OTP sent.");
    }

    // --- 5. RESET PASSWORD ---
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");

        User user = userOpt.get();

        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("OTP Expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok("Password changed successfully!");
    }
}