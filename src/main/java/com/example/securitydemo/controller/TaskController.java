package com.example.securitydemo.controller;

import com.example.securitydemo.dto.TaskDTO;
import com.example.securitydemo.model.Image;
import com.example.securitydemo.model.Task;
import com.example.securitydemo.model.User;
import com.example.securitydemo.repository.ImageRepository;
import com.example.securitydemo.repository.TaskRepository;
import com.example.securitydemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

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
    public String createTask(@ModelAttribute TaskDTO taskDTO,
//                             @RequestParam("images") MultipartFile[] images,
                             Principal principal)  throws IOException {
        User user = userRepository.findByUsername(principal.getName());
        Task task = new Task();
        task.setDescription(taskDTO.getDescription());
        task.setCompleted(false);
        task.setUser(user);


        // Save task first to get ID
        taskRepository.save(task);

        // Save images
        MultipartFile[] images = taskDTO.getImages();
        if (images != null && images.length > 0) {
            List<Image> imageList = new ArrayList<>();
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    Image image = new Image();
                    image.setFilename(file.getOriginalFilename());
                    image.setContentType(file.getContentType());
                    image.setData(file.getBytes());
                    image.setTask(task);
                    imageList.add(image);
                }
            }
            imageRepository.saveAll(imageList);
            task.setImages(imageList);
            taskRepository.save(task);
        }
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
    public String updateTask(@PathVariable Long id,
                             @ModelAttribute TaskDTO taskDTO,
//                             @RequestParam("images") MultipartFile[] images,
                             Principal principal) throws IOException {
        Task existingTask = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));
        if (!existingTask.getUser().getUsername().equals(principal.getName())) {
            return "redirect:/tasks"; // prevent updating others' tasks
        }
        existingTask.setDescription(taskDTO.getDescription());
        existingTask.setCompleted(taskDTO.isCompleted());

        // Delete selected images
        Long[] deleteImageIds = taskDTO.getDeleteImageIds();
        if (deleteImageIds != null) {
            List<Image> images = existingTask.getImages();
            images.removeIf(image -> {
                for (Long deleteId : deleteImageIds) {
                    if (image.getId().equals(deleteId)) {
                        imageRepository.delete(image);
                        return true; // remove from collection
                    }
                }
                return false;
            });
        }

        // Add new images
        MultipartFile[] images = taskDTO.getImages();
        if (images != null && images.length > 0) {
            List<Image> imageList = existingTask.getImages() != null ? existingTask.getImages() : new ArrayList<>();
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    Image image = new Image();
                    image.setFilename(file.getOriginalFilename());
                    image.setContentType(file.getContentType());
                    image.setData(file.getBytes());
                    image.setTask(existingTask);
                    imageList.add(image);
                }
            }
            imageRepository.saveAll(imageList);
            existingTask.setImages(imageList);
        }

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
