package com.example.securitydemo.controller;

import com.example.securitydemo.dto.TaskDTO;
import com.example.securitydemo.model.Task;
import com.example.securitydemo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public String listTasks(Model model, Principal principal,
                        @RequestParam(required = false) String description,
                        @RequestParam(required = false) Boolean completed,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            @RequestParam(defaultValue = "id") String sortField,
                            @RequestParam(defaultValue = "asc") String sortDir
                        ) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Task> taskPage = taskService.findTasks(principal.getName(), description, completed, pageable);

        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());
        model.addAttribute("filterDescription", description != null ? description : "");
        model.addAttribute("filterCompleted", completed);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "tasks";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new Task());
        return "task_form";
    }

    @PostMapping
    public String createTask(@ModelAttribute TaskDTO taskDTO,
//                           @RequestParam("images") MultipartFile[] images,
                             Principal principal)  throws IOException {
        taskService.createTask(taskDTO, principal.getName());

        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        Task task = taskService.getTaskById(id);
        if (!task.getUser().getUsername().equals(principal.getName())) {
            return "redirect:/tasks"; // prevent editing others' tasks
        }
        model.addAttribute("task", task);
        return "task_form";
    }

    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable Long id,
                             @ModelAttribute TaskDTO taskDTO,
//                             @RequestParam("images") MultipartFile[] images,
                             Principal principal) throws IOException {
        taskService.updateTask(id, taskDTO, principal.getName());

        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, Principal principal) {
        taskService.deleteTask(id, principal.getName());

        return "redirect:/tasks";
    }
}
