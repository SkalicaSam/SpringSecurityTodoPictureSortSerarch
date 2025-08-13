package com.example.securitydemo.config;

import com.example.securitydemo.model.User;
import com.example.securitydemo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (userRepository.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("adminPass"));
            admin.setEnabled(true);

            userRepository.save(admin);
        }

        if (userRepository.findByUsername("user1") == null) {
            User user = new User();
            user.setUsername("user1");
            user.setPassword(passwordEncoder.encode("password1"));
            user.setEnabled(true);

            userRepository.save(user);
        }

        if (userRepository.findByUsername("add") == null) {
            User admin = new User();
            admin.setUsername("add");
            admin.setPassword(passwordEncoder.encode("add"));
            admin.setEnabled(true);
            admin.setRoles(Set.of("ROLE_ADMIN", "ROLE_USER")); // admin has both roles

            userRepository.save(admin);
        }

        if (userRepository.findByUsername("us") == null) {
            User user = new User();
            user.setUsername("us");
            user.setPassword(passwordEncoder.encode("us"));
            user.setEnabled(true);
            user.setRoles(Set.of("ROLE_USER"));

            userRepository.save(user);
        }
    }
}
