package com.pizza.delivery.notification.model

import org.springframework.data.annotation.Id
import java.time.LocalDateTime

data class NotificationLog(
    @Id
    val id: String? = null, // MongoDB automatically handles String IDs (ObjectIds)
    val orderId: Long?,
    val pizzaName: String,
    val status: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
