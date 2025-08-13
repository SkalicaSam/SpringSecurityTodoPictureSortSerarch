package com.example.securitydemo.config;

import com.example.securitydemo.model.User;
import com.example.securitydemo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    }
}
