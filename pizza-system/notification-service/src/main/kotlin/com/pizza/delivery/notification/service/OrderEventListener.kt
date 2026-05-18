package com.pizza.delivery.notification.service

import com.pizza.delivery.notification.dto.PizzaOrderEvent
import com.pizza.delivery.notification.model.NotificationLog
import com.pizza.delivery.notification.repository.NotificationLogRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class OrderEventListener(
    private val objectMapper: ObjectMapper,
    private val logRepository: NotificationLogRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics= ["pizza-orders"], groupId = "pizza-notification-group")
    fun consumePizzaOrder(messageString: String) {
        try {
            val event = objectMapper.readValue(messageString, PizzaOrderEvent::class.java)

            logger.info("============== NEW NOTIFICATION ==============")
            logger.info("Received event for Order #${event.orderId}")
            logger.info("Kitchen status: [${event.status}] for pizza: ${event.pizzaName}")
            logger.info("===============================================")

            // Map the event to a Mongo Document and save it
            val logEntry = NotificationLog(orderId = event.orderId, pizzaName = event.pizzaName, status = event.status)
            val savedLog = logRepository.save(logEntry)
            logger.info("Successfully persisted notification audit log with ID: ${savedLog.id}")

        } catch (e: Exception) {
            logger.error("Error during order processing", e)
        }
    }

}