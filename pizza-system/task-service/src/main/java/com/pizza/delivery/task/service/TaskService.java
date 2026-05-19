package com.pizza.delivery.task.service;

import com.pizza.delivery.task.controller.TaskController;
import com.pizza.delivery.task.dto.PizzaOrderStatusChangedEvent;
import com.pizza.delivery.task.model.PizzaTask;
import com.pizza.delivery.task.model.PizzaTaskStatus;
import com.pizza.delivery.task.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
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
        kafkaTemplate.send("pizza-orders", String.valueOf(savedTask.getId()), preparingEvent);

        // 3. Kick off the async background baking loop
        ovenService.bakePizza(savedTask.getId());

        // 4. Return the entity instantly so the controller doesn't block
        return savedTask;

    }
}
