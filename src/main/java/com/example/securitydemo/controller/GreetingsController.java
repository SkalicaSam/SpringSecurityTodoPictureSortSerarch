package com.example.securitydemo.controller;


import com.example.securitydemo.model.User;
import com.example.securitydemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GreetingsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @PreAuthorize("hasRole('USER')")
//    @GetMapping("/hello")
//    public String sayHello(){
//        return "Hello";
//    }

    @GetMapping("/hello")
    public String sayHello(@RequestParam(required = false) String continueParam) {
        return "Hello";
    }


//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminEdnpoint(){
        return "Hello, admin";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               Model model) {
        if (userRepository.findByUsername(username) != null) {
            model.addAttribute("errorMessage", "Uživatel s tímto jménem již existuje.");
            return "register";  // show form again with error message
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);

        userRepository.save(user);

        model.addAttribute("successMessage", "Uživatel úspěšně vytvořen.");
        return "register";  // show form again with success message
    }

    @GetMapping("/register")
    public String redirectToRegister() {
        return "register";
    }

}
