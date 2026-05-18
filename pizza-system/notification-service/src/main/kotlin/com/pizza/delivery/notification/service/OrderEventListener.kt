package com.pizza.delivery.notification.service

import com.pizza.delivery.notification.dto.PizzaOrderEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class OrderEventListener(// Inject Spring's pre-configured ObjectMapper
    private val objectMapper: ObjectMapper
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
        } catch (e: Exception) {
            logger.error("Error during order processing", e)
        }
    }

}