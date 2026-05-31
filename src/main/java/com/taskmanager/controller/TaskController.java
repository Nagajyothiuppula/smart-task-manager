package com.taskmanager.controller;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import com.taskmanager.service.TaskService;
import com.taskmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    // ── Show Add Task Form ─────────────────────────────────
    @GetMapping("/new")
    public String showAddTaskForm(Model model) {
        model.addAttribute("task",       new Task());
        model.addAttribute("priorities", Task.Priority.values());
        model.addAttribute("statuses",   Task.Status.values());
        model.addAttribute("pageTitle",  "Add New Task");
        return "task/task-form";
    }

    // ── Create Task ────────────────────────────────────────
    @PostMapping("/new")
    public String createTask(
            @Valid @ModelAttribute("task") Task task,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("priorities", Task.Priority.values());
            model.addAttribute("statuses",   Task.Status.values());
            model.addAttribute("pageTitle",  "Add New Task");
            return "task/task-form";
        }

        User currentUser = userService.findByUsername(userDetails.getUsername());
        taskService.createTask(task, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Task created successfully!");
        return "redirect:/dashboard";
    }

    // ── Show Edit Task Form ────────────────────────────────
    @GetMapping("/edit/{id}")
    public String showEditTaskForm(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            User currentUser = userService.findByUsername(userDetails.getUsername());
            Task task = taskService.getTaskByIdAndUser(id, currentUser);

            model.addAttribute("task",       task);
            model.addAttribute("priorities", Task.Priority.values());
            model.addAttribute("statuses",   Task.Status.values());
            model.addAttribute("pageTitle",  "Edit Task");
            return "task/task-form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dashboard";
        }
    }

    // ── Update Task ────────────────────────────────────────
    @PostMapping("/edit/{id}")
    public String updateTask(
            @PathVariable Long id,
            @Valid @ModelAttribute("task") Task task,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("priorities", Task.Priority.values());
            model.addAttribute("statuses",   Task.Status.values());
            model.addAttribute("pageTitle",  "Edit Task");
            return "task/task-form";
        }

        try {
            User currentUser = userService.findByUsername(userDetails.getUsername());
            taskService.updateTask(id, task, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Task updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    // ── Delete Task ────────────────────────────────────────
    @PostMapping("/delete/{id}")
    public String deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            User currentUser = userService.findByUsername(userDetails.getUsername());
            taskService.deleteTask(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Task deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard";
    }
}
