package com.hrms.controller;

import com.hrms.dto.LoginRequest;
import com.hrms.dto.LoginResponse;
import com.hrms.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Login API
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    // Create first ADMIN account
    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdmin(
            @RequestHeader("Secret-Key") String secretKey,
            @Valid @RequestBody LoginRequest request) {

        if (!"HRMS@2026".equals(secretKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Invalid secret key");
        }

        authService.createAdmin(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Admin created successfully");
    }
    @PostMapping("/create-staff")
    public ResponseEntity<String> createStaff(
            @RequestBody LoginRequest request) {

        authService.createStaffUser(
                request.getEmail().split("@")[0],
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Staff user created successfully");
    }
}