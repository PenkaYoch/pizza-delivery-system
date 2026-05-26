package com.pizza.delivery.task.controller;

import com.pizza.delivery.task.model.PizzaTask;
import com.pizza.delivery.task.model.PizzaTaskStatus;
import com.pizza.delivery.task.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<PizzaTask>> getTasks() {
        return ResponseEntity.ok(taskService.getTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PizzaTask> getTask(@PathVariable Long id) {
        return taskService.getTask(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if(taskService.deleteTask(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PizzaTask> updateTask(@PathVariable Long id, @RequestBody UpdateTaskRequest request) {
        return taskService.updateTask(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record OrderRequest(String name) {
    }

    public record UpdateTaskRequest(String pizzaName, PizzaTaskStatus status){
    }
}
