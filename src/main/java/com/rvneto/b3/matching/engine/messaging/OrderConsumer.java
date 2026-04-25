package com.rvneto.b3.matching.engine.messaging;

import com.rvneto.b3.matching.engine.dto.OrderEventDTO;
import com.rvneto.b3.matching.engine.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {

    private final MatchingService matchingService;

    @RabbitListener(queues = "${app.rabbitmq.queue-in}")
    public void receiveOrder(OrderEventDTO event) {
        log.info("Nova ordem recebida da corretora: ID {} | Ticker {} | Lado {}", event.getOrderId(), event.getTicker(), event.getSide());

        // Encaminha para o motor de matching
        matchingService.process(event);
    }
}