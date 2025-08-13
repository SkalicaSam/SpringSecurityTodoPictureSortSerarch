package com.example.securitydemo.controller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getTasks_Unauthenticated_RedirectsToLogin() {
        ResponseEntity<String> response = restTemplate.getForEntity("/tasks", String.class);
        assertThat(response.getStatusCode().is3xxRedirection()).isTrue();
        assertThat(response.getHeaders().getLocation().toString()).contains("/login");
    }

    @Test
    void getTasks_Authenticated_ReturnsOkAndPageContent() {
        // Prepare headers with basic auth (adjust username/password as per your setup)
        TestRestTemplate authenticatedRestTemplate = restTemplate.withBasicAuth("admin", "1234");

        ResponseEntity<String> response = authenticatedRestTemplate.getForEntity("/hello", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Hello");
    }



}
