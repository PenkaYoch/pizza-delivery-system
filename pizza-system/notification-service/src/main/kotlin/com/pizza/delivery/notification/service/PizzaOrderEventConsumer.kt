package com.pizza.delivery.notification.service

import com.pizza.delivery.notification.dto.PizzaOrderEvent
import com.pizza.delivery.notification.model.NotificationLog
import com.pizza.delivery.notification.repository.NotificationLogRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class PizzaOrderEventConsumer(
    private val notificationLogRepository: NotificationLogRepository
) {
    private val log = LoggerFactory.getLogger(PizzaOrderEventConsumer::class.java)

    @KafkaListener(topics = ["pizza-orders"], groupId = "\${spring.kafka.consumer.group-id}")
    fun consume(orderEvent: PizzaOrderEvent) {
        val notification = notificationLogRepository.save(
            NotificationLog(
                orderId = orderEvent.orderId,
                pizzaName = orderEvent.pizzaName,
                status = orderEvent.status
            )
        )

        log.info("Saved notification ${notification.id} for pizza ${orderEvent.pizzaName} with status ${orderEvent.status}")
    }
}