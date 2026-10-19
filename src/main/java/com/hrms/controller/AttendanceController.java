
package com.hrms.controller;

import com.hrms.entity.Attendance;
import com.hrms.entity.Staff;
import com.hrms.repository.AttendanceRepository;
import com.hrms.repository.StaffRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/staff")
public class AttendanceController {

    private final AttendanceRepository attendanceRepository;
    private final StaffRepository staffRepository;

    public AttendanceController(
            AttendanceRepository attendanceRepository,
            StaffRepository staffRepository) {
        this.attendanceRepository = attendanceRepository;
        this.staffRepository = staffRepository;
    }

    // Find the logged-in staff member
    private Staff getLoggedInStaff(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        return staffRepository.findAll()
                .stream()
                .filter(staff ->
                        staff.getUser() != null
                                && authentication.getName().equals(
                                staff.getUser().getEmail()))
                .findFirst()
                .orElse(null);
    }

    // Staff check-in
    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(
            Authentication authentication) {

        Staff staff = getLoggedInStaff(authentication);

        if (staff == null) {
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "message",
                            "Staff profile not found"));
        }

        LocalDate today = LocalDate.now();

        boolean alreadyCheckedIn = attendanceRepository.findAll()
                .stream()
                .anyMatch(a ->
                        a.getStaff().getId().equals(staff.getId())
                                && a.getAttendanceDate().equals(today));

        if (alreadyCheckedIn) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Already checked in today"));
        }

        Attendance attendance = new Attendance();
        attendance.setStaff(staff);
        attendance.setAttendanceDate(today);
        attendance.setCheckIn(LocalTime.now());
        attendance.setWorkingHours(0.0);

        return ResponseEntity.ok(
                attendanceRepository.save(attendance));
    }

    // Staff check-out
    @PostMapping("/check-out")
    public ResponseEntity<?> checkOut(
            Authentication authentication) {

        Staff staff = getLoggedInStaff(authentication);

        if (staff == null) {
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "message",
                            "Staff profile not found"));
        }

        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository.findAll()
                .stream()
                .filter(a ->
                        a.getStaff().getId().equals(staff.getId())
                                && a.getAttendanceDate().equals(today)
                                && a.getCheckOut() == null)
                .findFirst()
                .orElse(null);

        if (attendance == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Please check in first"));
        }

        LocalTime checkOutTime = LocalTime.now();
        attendance.setCheckOut(checkOutTime);

        if (attendance.getCheckIn() != null) {
            double hours = Duration.between(
                    attendance.getCheckIn(),
                    checkOutTime
            ).toMillis() / 3_600_000.0;

            attendance.setWorkingHours(
                    Math.max(0.0, hours));
        }

        return ResponseEntity.ok(
                attendanceRepository.save(attendance));
    }

    // Get logged-in staff attendance history
    @GetMapping("/attendance")
    public ResponseEntity<?> getMyAttendance(
            Authentication authentication) {

        Staff staff = getLoggedInStaff(authentication);

        if (staff == null) {
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "message",
                            "Staff profile not found"));
        }

        List<Attendance> records = attendanceRepository.findAll()
                .stream()
                .filter(a ->
                        a.getStaff().getId().equals(staff.getId()))
                .toList();

        return ResponseEntity.ok(records);
    }
}
