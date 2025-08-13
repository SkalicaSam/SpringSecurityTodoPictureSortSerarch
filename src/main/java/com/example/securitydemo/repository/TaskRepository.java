package com.example.securitydemo.repository;

import com.example.securitydemo.model.Task;
import com.example.securitydemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);

    // Find by user and description containing keyword (case insensitive)
    List<Task> findByUserAndDescriptionContainingIgnoreCase(User user, String description);

    // Find by user and completed status
    List<Task> findByUserAndCompleted(User user, boolean completed);

    // Find by user, description containing keyword and completed status
    @Query("SELECT t FROM Task t WHERE t.user = :user AND (:description IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND (:completed IS NULL OR t.completed = :completed)")
    List<Task> findByUserAndDescriptionAndCompleted(@Param("user") User user,
                                                    @Param("description") String description,
                                                    @Param("completed") Boolean completed);

}
