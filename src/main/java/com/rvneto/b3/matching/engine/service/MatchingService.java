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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {

    private final MarketPriceService marketPriceService;
    private final OrderExecutionRepository repository;
    private final OrderProducer orderProducer;

    public void process(OrderEventDTO order) {
        log.info("Processando match para a ordem {}: {} {}", order.getOrderId(), order.getSide(), order.getTicker());

        // 1. Busca o preço atual no Redis
        var marketDataOpt = marketPriceService.getCurrentPrice(order.getTicker());

        if (marketDataOpt.isEmpty()) {
            log.warn("Match abortado: Ticker {} nao encontrado no cache.", order.getTicker());
            return;
        }

        BigDecimal marketPrice = marketDataOpt.get().getRegularMarketPrice();

        if (isCanExecute(order, marketPrice)) {
            log.info("MATCH SUCESSO: Ordem {} executada a R$ {}", order.getOrderId(), marketPrice);
            saveAndNotify(order, marketPrice, ExecutionStatus.FILLED);
        } else {
            log.info("MATCH NEGADO: Preco da ordem (R$ {}) fora do valor de mercado (R$ {})", order.getPrice(), marketPrice);
            saveAndNotify(order, marketPrice, ExecutionStatus.REJECTED);
        }
    }

    private static boolean isCanExecute(OrderEventDTO order, BigDecimal marketPrice) {
        boolean canExecute = false;

        if (SideStatus.BUY.name().equalsIgnoreCase(order.getSide())) {
            // Só executa a compra se o preço que o usuário aceita pagar (order.price) for maior ou igual ao preço atual de mercado.
            canExecute = order.getPrice().compareTo(marketPrice) >= 0;
        } else if (SideStatus.SELL.name().equalsIgnoreCase(order.getSide())) {
            // Só executa a venda se o preço que o usuário quer receber (order.price) for menor ou igual ao preço atual de mercado.
            canExecute = order.getPrice().compareTo(marketPrice) <= 0;
        }
        return canExecute;
    }

    private void saveAndNotify(OrderEventDTO order, BigDecimal price, ExecutionStatus status) {
        // 1. Persistência no PostgreSQL
        OrderExecution execution = OrderExecution.builder()
                .orderId(order.getOrderId())
                .ticker(order.getTicker())
                .side(SideStatus.valueOf(order.getSide()))
                .quantity(order.getQuantity())
                .executedPrice(price)
                .status(status)
                .build();

        repository.save(execution);

        // 2. Notificação para o Broker (Fila mq-b3-to-broker)
        OrderResponseEvent response = new OrderResponseEvent(order.getOrderId(), status.name(), price);
        orderProducer.sendToBroker(response);
    }
}