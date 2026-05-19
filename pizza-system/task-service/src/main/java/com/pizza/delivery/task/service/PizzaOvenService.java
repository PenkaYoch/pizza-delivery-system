package com.pizza.delivery.task.service;

import com.pizza.delivery.task.dto.PizzaOrderStatusChangedEvent;
import com.pizza.delivery.task.model.PizzaTask;
import com.pizza.delivery.task.model.PizzaTaskStatus;
import com.pizza.delivery.task.repository.TaskRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PizzaOvenService {

    private final TaskRepository repository;
    private final KafkaTemplate<String, PizzaOrderStatusChangedEvent> kafkaTemplate;

    public PizzaOvenService(TaskRepository repository, KafkaTemplate<String, PizzaOrderStatusChangedEvent> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public void bakePizza(Long taskId) {
        try {
            // 1. Simulate the baking duration (15 seconds)
            Thread.sleep(15000);

            // 2. Fetch the fresh state of the task
            PizzaTask task = repository.findById(taskId)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

            // 3. Update the state to Ready
            task.setStatus(PizzaTaskStatus.READY);
            repository.saveAndFlush(task);

            // 4. Broadcast the new ready status to Kafka
            PizzaOrderStatusChangedEvent readyEvent = new PizzaOrderStatusChangedEvent(task.getId(), task.getPizzaName(), task.getStatus().name());
            kafkaTemplate.send("pizza-orders", String.valueOf(task.getId()), readyEvent);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Baking process was interrupted", e);
        }
    }
}
