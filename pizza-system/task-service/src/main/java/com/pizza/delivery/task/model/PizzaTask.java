package com.pizza.delivery.task.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
    private String status; // preparing, ready
    private LocalDateTime createdAt = LocalDateTime.now();
}
