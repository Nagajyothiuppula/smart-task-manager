package com.taskmanager.controller;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import com.taskmanager.service.TaskService;
import com.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TaskService taskService;
    private final UserService userService;

    @GetMapping
    public String showDashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "search", required = false) String search,
            Model model) {

        User currentUser = userService.findByUsername(userDetails.getUsername());

        List<Task> tasks;
        if (search != null && !search.isBlank()) {
            tasks = taskService.searchTasks(currentUser, search);
            model.addAttribute("searchKeyword", search);
        } else {
            tasks = taskService.getAllTasksByUser(currentUser);
        }

        // Stats for summary cards
        model.addAttribute("totalTasks",     taskService.getTotalTaskCount(currentUser));
        model.addAttribute("completedTasks", taskService.getCompletedTaskCount(currentUser));
        model.addAttribute("pendingTasks",   taskService.getPendingTaskCount(currentUser));
        model.addAttribute("inProgressTasks",taskService.getInProgressTaskCount(currentUser));

        model.addAttribute("tasks",       tasks);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("priorities",  Task.Priority.values());
        model.addAttribute("statuses",    Task.Status.values());

        return "dashboard";
    }
}
