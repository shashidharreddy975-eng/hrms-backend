
package com.hrms.controller;

import com.hrms.entity.Staff;
import com.hrms.entity.User;
import com.hrms.repository.StaffRepository;
import com.hrms.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/staff")
public class StaffController {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;

    public StaffController(
            StaffRepository staffRepository,
            UserRepository userRepository) {
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {

        String email = authentication.getName();

        Optional<User> userOptional =
                userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();

        Optional<Staff> staffOptional =
                staffRepository.findAll().stream()
                        .filter(s -> s.getUser() != null
                                && s.getUser().getId().equals(user.getId()))
                        .findFirst();

        if (staffOptional.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("message",
                            "Staff profile not found for this user"));
        }

        return ResponseEntity.ok(staffOptional.get());
    }
}
