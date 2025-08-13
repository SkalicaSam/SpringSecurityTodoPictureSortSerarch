package com.example.securitydemo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) ->
                requests.requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/register", "/register/**").permitAll()
                        .anyRequest().authenticated());

//        Remove stateless session management to enable session support
//        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // add this line to work API without Cookies

        http.formLogin(form -> form    //  builded login page
                .defaultSuccessUrl("/hello", true)  // Redirect to /hello after successful login
        );
//        http.formLogin(withDefaults());//  builded login page
//        http.httpBasic(withDefaults());   // with login popup // // HTTP Basic authentication with session support


        // Přidání podpory pro odhlášení
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/hello") // Redirect after logout
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
        );

        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
        http.csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



}
