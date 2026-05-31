package com.taskmanager.service;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.Task.Status;
import com.taskmanager.entity.User;
import com.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional
    public Task createTask(Task task, User user) {
        task.setUser(user);
        return taskRepository.save(task);
    }

    public List<Task> getAllTasksByUser(User user) {
        return taskRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Task getTaskByIdAndUser(Long id, User user) {
        return taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Task not found or access denied."));
    }

    @Transactional
    public Task updateTask(Long id, Task updatedTask, User user) {
        Task existing = getTaskByIdAndUser(id, user);
        existing.setTitle(updatedTask.getTitle());
        existing.setDescription(updatedTask.getDescription());
        existing.setPriority(updatedTask.getPriority());
        existing.setStatus(updatedTask.getStatus());
        return taskRepository.save(existing);
    }

    @Transactional
    public void deleteTask(Long id, User user) {
        Task task = getTaskByIdAndUser(id, user);
        taskRepository.delete(task);
    }

    // ── Dashboard Stats ────────────────────────────────────
    public long getTotalTaskCount(User user) {
        return taskRepository.countByUser(user);
    }

    public long getCompletedTaskCount(User user) {
        return taskRepository.countByUserAndStatus(user, Status.COMPLETED);
    }

    public long getPendingTaskCount(User user) {
        return taskRepository.countByUserAndStatus(user, Status.PENDING);
    }

    public long getInProgressTaskCount(User user) {
        return taskRepository.countByUserAndStatus(user, Status.IN_PROGRESS);
    }

    public List<Task> searchTasks(User user, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllTasksByUser(user);
        }
        return taskRepository.searchByUserAndKeyword(user, keyword.trim());
    }
}
