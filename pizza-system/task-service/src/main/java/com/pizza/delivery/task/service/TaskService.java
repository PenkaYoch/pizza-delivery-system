package com.pizza.delivery.task.service;

import com.pizza.delivery.task.controller.TaskController;
import com.pizza.delivery.task.dto.PizzaOrderStatusChangedEvent;
import com.pizza.delivery.task.model.PizzaTask;
import com.pizza.delivery.task.model.PizzaTaskStatus;
import com.pizza.delivery.task.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository repository;
    private final KafkaTemplate<String, PizzaOrderStatusChangedEvent> kafkaTemplate;
    private final PizzaOvenService ovenService;


    public TaskService(TaskRepository repository, KafkaTemplate<String, PizzaOrderStatusChangedEvent> kafkaTemplate, PizzaOvenService ovenService) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.ovenService = ovenService;
    }

    @Transactional
    public PizzaTask createPizzaTask(TaskController.OrderRequest request) {
        // 1. Save initial entity as Preparing
        PizzaTask task = new PizzaTask();
        task.setPizzaName(request.name());
        task.setStatus(PizzaTaskStatus.PREPARING);
        PizzaTask savedTask = repository.saveAndFlush(task);

        // 2. Broadcast Preparing state to Kafka immediately
        PizzaOrderStatusChangedEvent preparingEvent = new PizzaOrderStatusChangedEvent(savedTask.getId(), savedTask.getPizzaName(), savedTask.getStatus().name());
        publishStatusChanged(savedTask.getId(), preparingEvent);

        // 3. Kick off the async background baking loop
        ovenService.bakePizza(savedTask.getId());

        // 4. Return the entity instantly so the controller doesn't block
        return savedTask;

    }

    public List<PizzaTask> getTasks() {
        return repository.findAll();
    }

    public Optional<PizzaTask> getTask(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public boolean deleteTask(Long id) {
        return repository.findById(id)
                .map(task -> {
                    publishStatusChanged(task.getId(), new PizzaOrderStatusChangedEvent(task.getId(), task.getPizzaName(), PizzaTaskStatus.DELETED.name()));
                    repository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    public Optional<PizzaTask> updateTask(Long id, TaskController.UpdateTaskRequest updateTaskRequest) {
        return repository.findById(id)
                .map(task -> {

                    if (updateTaskRequest.pizzaName() != null && !updateTaskRequest.pizzaName().isBlank()) {
                        task.setPizzaName(updateTaskRequest.pizzaName().trim());
                    }

                    if (updateTaskRequest.status() != null) {
                        task.setStatus(updateTaskRequest.status());
                    }

                    PizzaTask savedTask = repository.saveAndFlush(task);

                    PizzaTaskStatus previousStatus = task.getStatus();
                    if (updateTaskRequest.status() != null && updateTaskRequest.status() != previousStatus) {
                        publishStatusChanged(savedTask.getId(), new PizzaOrderStatusChangedEvent(savedTask.getId(), savedTask.getPizzaName(), savedTask.getStatus().name()));
                    }

                    return savedTask;
                });
    }

    private void publishStatusChanged(Long taskId, PizzaOrderStatusChangedEvent event) {
        try {
            kafkaTemplate.send("pizza-orders", String.valueOf(taskId), event)
                    .exceptionally(ex -> {
                        log.warn("Failed to publish pizza task status event for task {}", taskId, ex);
                        return null;
                    });
        } catch (RuntimeException ex) {
            log.warn("Failed to publish pizza task status event for task {}", taskId, ex);
        }
    }
}
