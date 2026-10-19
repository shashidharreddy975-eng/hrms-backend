
package com.hrms.service;

import com.hrms.entity.User;
import com.hrms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class StaffPasswordFix {

    @Bean
    CommandLineRunner fixStaffPassword(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            User user = userRepository.findByEmail("staff@hrms.com")
                    .orElseThrow(() ->
                            new RuntimeException("Staff user not found"));

            user.setPassword(passwordEncoder.encode("Staff@12345"));
            user.setActive(true);
            userRepository.save(user);

            System.out.println("Staff password reset completed");
        };
    }
}
