package com.example.securitydemo.repository;

import com.example.securitydemo.model.Task;
import com.example.securitydemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
}
