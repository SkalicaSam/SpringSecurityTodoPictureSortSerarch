package com.example.securitydemo.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        model.addAttribute("errorMessage", "Page not found");
        return "error/404"; // create this Thymeleaf template
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/400"; // create this Thymeleaf template
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("errorMessage", "An unexpected error occurred");
        return "error/500"; // create this Thymeleaf template
    }
}
