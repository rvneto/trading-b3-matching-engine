package com.rvneto.b3.matching.engine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.queue-in}")
    private String queueIn;

    @Value("${app.rabbitmq.queue-out}")
    private String queueOut;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${app.rabbitmq.dlx}")
    private String dlxName;

    @Value("${app.rabbitmq.dlq}")
    private String dlqName;

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(dlxName);
    }

    @Bean
    public Queue queueIn() {
        return QueueBuilder.durable(queueIn)
                .withArgument("x-dead-letter-exchange", dlxName)
                .withArgument("x-dead-letter-routing-key", dlqName)
                .build();
    }

    @Bean
    public Queue queueOut() {
        return new Queue(queueOut, true);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(dlqName).build();
    }

    @Bean
    public Binding bindingQueueIn(Queue queueIn, DirectExchange exchange) {
        return BindingBuilder.bind(queueIn).to(exchange).with(routingKey);
    }

    @Bean
    public Binding bindingDlq(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(dlqName);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}