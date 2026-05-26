package com.pizza.delivery.task.service;

import com.pizza.delivery.task.dto.PizzaOrderStatusChangedEvent;
import com.pizza.delivery.task.model.PizzaTask;
import com.pizza.delivery.task.model.PizzaTaskStatus;
import com.pizza.delivery.task.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PizzaOvenService {
    private static final Logger log = LoggerFactory.getLogger(PizzaOvenService.class);
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
                    .orElse(null);

            if(task == null) {
                log.info("Skipping bake completion for deleted task {}", taskId);
                return;
            }

            // 3. Update the state to Ready
            task.setStatus(PizzaTaskStatus.READY);
            repository.saveAndFlush(task);

            // 4. Broadcast the new ready status to Kafka
            PizzaOrderStatusChangedEvent readyEvent = new PizzaOrderStatusChangedEvent(task.getId(), task.getPizzaName(), task.getStatus().name());
            publishStatusChanged(task.getId(), readyEvent);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Baking process was interrupted", e);
        }
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
