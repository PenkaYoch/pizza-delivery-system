package com.pizza.delivery.task.dto;

public record PizzaOrderEvent(
        Long orderId,
        String pizzaName,
        String status
) {}
