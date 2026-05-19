package com.pizza.delivery.task.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class PizzaTask {
    @Id
    @GeneratedValue
    private Long id;
    private String pizzaName;
    @Enumerated(EnumType.STRING)
    private PizzaTaskStatus status;
    private LocalDateTime createdAt = LocalDateTime.now();
}
