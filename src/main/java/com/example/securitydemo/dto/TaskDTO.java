package com.example.securitydemo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class TaskDTO {

    private Long id;
    private String description;
    private boolean completed;
    private MultipartFile[] images;
    private Long[] deleteImageIds;

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public MultipartFile[] getImages() {
        return images;
    }

    public void setImages(MultipartFile[] images) {
        this.images = images;
    }

    public Long[] getDeleteImageIds() {
        return deleteImageIds;
    }

    public void setDeleteImageIds(Long[] deleteImageIds) {
        this.deleteImageIds = deleteImageIds;
    }


}
