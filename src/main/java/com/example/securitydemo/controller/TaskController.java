package com.example.securitydemo.controller;

import com.example.securitydemo.model.Task;
import com.example.securitydemo.model.User;
import com.example.securitydemo.repository.TaskRepository;
import com.example.securitydemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listTasks(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        List<Task> tasks = taskRepository.findByUser(user);
        model.addAttribute("tasks", tasks);
        return "tasks";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new Task());
        return "task_form";
    }

    @PostMapping
    public String createTask(@ModelAttribute Task task, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        task.setUser(user);
        task.setCompleted(false);
        taskRepository.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));
        if (!task.getUser().getUsername().equals(principal.getName())) {
            return "redirect:/tasks"; // prevent editing others' tasks
        }
        model.addAttribute("task", task);
        return "task_form";
    }

    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task task, Principal principal) {
        Task existingTask = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));
        if (!existingTask.getUser().getUsername().equals(principal.getName())) {
            return "redirect:/tasks"; // prevent updating others' tasks
        }
        existingTask.setDescription(task.getDescription());
        existingTask.setCompleted(task.isCompleted());
        taskRepository.save(existingTask);
        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, Principal principal) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));
        if (task.getUser().getUsername().equals(principal.getName())) {
            taskRepository.delete(task);
        }
        return "redirect:/tasks";
    }
}
