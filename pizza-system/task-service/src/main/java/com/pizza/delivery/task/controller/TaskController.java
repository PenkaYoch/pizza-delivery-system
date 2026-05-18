package com.pizza.delivery.task.controller;

import com.pizza.delivery.task.dto.PizzaOrderEvent;
import com.pizza.delivery.task.model.PizzaTask;
import com.pizza.delivery.task.repository.TaskRepository;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository repository;
    private final KafkaTemplate<String, PizzaOrderEvent> kafkaTemplate;

    public TaskController(TaskRepository repository, KafkaTemplate<String, PizzaOrderEvent> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public ResponseEntity<PizzaTask> createOrder(@RequestBody OrderRequest request) {
        // save in PostgreSQL
        PizzaTask task = new PizzaTask();
        task.setPizzaName(request.name);
        task.setStatus("Preparing");

        PizzaTask savedTask = repository.saveAndFlush(task);

        // map to event payload
        PizzaOrderEvent orderEvent = new PizzaOrderEvent(
                savedTask.getId(),
                savedTask.getPizzaName(),
                savedTask.getStatus()
        );

        // publish to kafka topic using orderID as messageID in the kafka topic
        kafkaTemplate.send("pizza-orders", String.valueOf(savedTask.getId()), orderEvent);

        return ResponseEntity.ok(savedTask);
    }

    public record OrderRequest(String name) {
    }
}
