package com.pizza.delivery.task.controller;

import com.pizza.delivery.task.model.PizzaTask;
import com.pizza.delivery.task.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<PizzaTask> createOrder(@RequestBody OrderRequest request) {
        PizzaTask processingTask = taskService.createPizzaTask(request);
        return ResponseEntity.ok(processingTask);
    }

    public record OrderRequest(String name) {
    }
}
