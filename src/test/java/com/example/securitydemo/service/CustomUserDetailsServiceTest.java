package com.example.securitydemo.service;

import com.example.securitydemo.model.User;
import com.example.securitydemo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {
    // Unit Tests
    //Testují jednotlivé třídy/metody izolovaně

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        UserRepository userRepository = mock(UserRepository.class);
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEnabled(true);

        when(userRepository.findByUsername("testuser")).thenReturn(user);

        CustomUserDetailsService service = new CustomUserDetailsService(userRepository);
        var userDetails = service.loadUserByUsername("testuser");

        assertEquals("testuser", userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        CustomUserDetailsService service = new CustomUserDetailsService(userRepository);

        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername("unknown");
        });
    }
}