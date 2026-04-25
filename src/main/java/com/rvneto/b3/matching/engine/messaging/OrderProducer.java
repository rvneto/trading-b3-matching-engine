package com.rvneto.b3.matching.engine.messaging;

import com.rvneto.b3.matching.engine.dto.OrderResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.queue-out}")
    private String queueName;

    public void sendToBroker(OrderResponseEvent message) {
        log.info("Sending order response {} to broker queue: {}", message.getOrderId(), queueName);
        rabbitTemplate.convertAndSend(queueName, message);
    }

}
