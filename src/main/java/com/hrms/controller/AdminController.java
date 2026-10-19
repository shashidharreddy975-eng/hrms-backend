
        package com.hrms.controller;

import com.hrms.entity.Staff;
import com.hrms.entity.User;
import com.hrms.repository.StaffRepository;
import com.hrms.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;

    public AdminController(
            StaffRepository staffRepository,
            UserRepository userRepository) {
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public Map<String, String> dashboard() {
        return Map.of(
                "role", "ADMIN",
                "message", "Welcome to HRMS Admin Dashboard"
        );
    }

    @PostMapping("/staff")
    public ResponseEntity<?> createStaff(@RequestBody Staff staff) {

        if (staff.getUser() == null ||
                staff.getUser().getId() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "User ID is required"));
        }

        Long userId = staff.getUser().getId();

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "User not found"));
        }

        if (staffRepository.existsByUser_Id(userId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error",
                            "This user already has a staff record"));
        }

        staff.setId(null);
        staff.setUser(user);

        Staff saved = staffRepository.save(staff);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/staff")
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    @GetMapping("/staff/{id}")
    public ResponseEntity<Staff> getStaffById(@PathVariable Long id) {
        return staffRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/staff/{id}")
    public ResponseEntity<Staff> updateStaff(
            @PathVariable Long id,
            @RequestBody Staff updatedStaff) {

        return staffRepository.findById(id).map(existing -> {
            existing.setPhone(updatedStaff.getPhone());
            existing.setDepartment(updatedStaff.getDepartment());
            existing.setDesignation(updatedStaff.getDesignation());
            existing.setJoiningDate(updatedStaff.getJoiningDate());

            return ResponseEntity.ok(staffRepository.save(existing));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/staff/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        if (!staffRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        staffRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
