package com.pizza.delivery.notification.controller

import com.pizza.delivery.notification.model.NotificationLog
import com.pizza.delivery.notification.repository.NotificationLogRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationLogRepository: NotificationLogRepository
) {

    @GetMapping
    fun getNotifications(): List<NotificationLog> = notificationLogRepository.findAll()
}