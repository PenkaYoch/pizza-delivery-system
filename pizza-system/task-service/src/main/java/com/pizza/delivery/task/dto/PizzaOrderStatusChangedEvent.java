package com.pizza.delivery.task.dto;

public record PizzaOrderStatusChangedEvent(
        Long orderId,
        String pizzaName,
        String status
) {}
