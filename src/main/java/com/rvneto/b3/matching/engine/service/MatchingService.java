package com.rvneto.b3.matching.engine.service;

import com.rvneto.b3.matching.engine.dto.OrderEventDTO;
import com.rvneto.b3.matching.engine.dto.OrderResponseEvent;
import com.rvneto.b3.matching.engine.messaging.OrderProducer;
import com.rvneto.b3.matching.engine.model.ExecutionStatus;
import com.rvneto.b3.matching.engine.model.OrderExecution;
import com.rvneto.b3.matching.engine.model.SideStatus;
import com.rvneto.b3.matching.engine.repository.OrderExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {

    private final MarketPriceService marketPriceService;
    private final OrderExecutionRepository repository;
    private final OrderProducer orderProducer;

    public void process(OrderEventDTO order) {
        log.info("Processing match for order {}: {} {}", order.getOrderId(), order.getSide(), order.getTicker());

        // 1. Fetches the current price from Redis
        var marketDataOpt = marketPriceService.getCurrentPrice(order.getTicker());

        if (marketDataOpt.isEmpty()) {
            log.warn("Match aborted: Ticker {} not found in cache.", order.getTicker());
            OrderResponseEvent rejection = new OrderResponseEvent(order.getOrderId(), "REJECTED", BigDecimal.ZERO);
            orderProducer.sendToBroker(rejection);
            return;
        }

        BigDecimal marketPrice = marketDataOpt.get().getRegularMarketPrice();

        if (isCanExecute(order, marketPrice)) {
            log.info("MATCH SUCCESS: Order {} executed at R$ {}", order.getOrderId(), marketPrice);
            saveAndNotify(order, marketPrice, ExecutionStatus.FILLED);
        } else {
            log.info("MATCH REJECTED: Order price (R$ {}) outside market value (R$ {})", order.getPrice(), marketPrice);
            saveAndNotify(order, marketPrice, ExecutionStatus.REJECTED);
        }
    }

    private static boolean isCanExecute(OrderEventDTO order, BigDecimal marketPrice) {
        boolean canExecute = false;

        if (SideStatus.BUY.name().equalsIgnoreCase(order.getSide())) {
            // Only executes the buy if the price the user is willing to pay (order.price) is greater than or equal to the current market price.
            canExecute = order.getPrice().compareTo(marketPrice) >= 0;
        } else if (SideStatus.SELL.name().equalsIgnoreCase(order.getSide())) {
            // Only executes the sell if the price the user wants to receive (order.price) is less than or equal to the current market price.
            canExecute = order.getPrice().compareTo(marketPrice) <= 0;
        }
        return canExecute;
    }

    @Transactional
    private void saveAndNotify(OrderEventDTO order, BigDecimal price, ExecutionStatus status) {
        // 1. Persistence in PostgreSQL
        OrderExecution execution = OrderExecution.builder()
                .orderId(order.getOrderId())
                .ticker(order.getTicker())
                .side(SideStatus.valueOf(order.getSide()))
                .quantity(order.getQuantity())
                .executedPrice(price)
                .status(status)
                .build();

        repository.save(execution);

        // 2. Notification to the Broker (Queue mq-b3-to-broker)
        OrderResponseEvent response = new OrderResponseEvent(order.getOrderId(), status.name(), price);
        orderProducer.sendToBroker(response);
    }
}