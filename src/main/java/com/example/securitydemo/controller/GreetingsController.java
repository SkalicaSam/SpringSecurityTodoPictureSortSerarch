package com.example.securitydemo.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingsController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/hello")
    public String sayHello(){
        return "Hello";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminEdnpoint(){
        return "Hello, admin";
    }

}
