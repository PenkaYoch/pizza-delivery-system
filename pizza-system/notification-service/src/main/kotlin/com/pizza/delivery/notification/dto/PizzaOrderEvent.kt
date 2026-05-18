package com.pizza.delivery.notification.dto

data class PizzaOrderEvent(
    val orderId: Long = 0,
    val pizzaName: String = "",
    val status: String = ""
)