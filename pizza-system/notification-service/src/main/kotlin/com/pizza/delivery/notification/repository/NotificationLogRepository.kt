package com.pizza.delivery.notification.repository

import com.pizza.delivery.notification.model.NotificationLog
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationLogRepository: MongoRepository<NotificationLog, String> {
    // Inherits basic CRUD methods like save(), findAll(), findById() out of the box
}