package com.example.securitydemo.service;

import com.example.securitydemo.dto.TaskDTO;
import com.example.securitydemo.model.Image;
import com.example.securitydemo.model.Task;
import com.example.securitydemo.model.User;
import com.example.securitydemo.repository.ImageRepository;
import com.example.securitydemo.repository.TaskRepository;
import com.example.securitydemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    public Page<Task> findTasks(String username, String description, Boolean completed, Pageable pageable) {
        User user = userRepository.findByUsername(username);
        return taskRepository.findByUserAndDescriptionAndCompleted(user, description, completed, pageable);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));
    }

    @Transactional
    public void createTask(TaskDTO taskDTO, String username) throws IOException {
        User user = userRepository.findByUsername(username);
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
    }

    @Transactional
    public void updateTask(Long id, TaskDTO taskDTO, String username) throws IOException {
        Task existingTask = getTaskById(id);
        if (!existingTask.getUser().getUsername().equals(username)) {
            // Or throw an AccessDeniedException
            return;
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
    }

    public void deleteTask(Long id, String username) {
        Task task = getTaskById(id);
        if (task.getUser().getUsername().equals(username)) {
            taskRepository.delete(task);
        }
    }
}

