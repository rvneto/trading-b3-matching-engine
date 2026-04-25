package com.rvneto.b3.matching.engine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rvneto.b3.matching.engine.dto.MarketDataDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class MarketPriceService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_KEY_PREFIX = "market:price:";

    public MarketPriceService(@Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Optional<MarketDataDTO> getCurrentPrice(String ticker) {

        String key = CACHE_KEY_PREFIX + ticker.toUpperCase().trim();

        log.info("Serializer de chave atual: {}", redisTemplate.getKeySerializer().getClass().getName());
        log.info("Buscando chave: [{}]", key);

        Object data = redisTemplate.opsForValue().get(key);

        if (isNull(data)) {
            log.warn("Preco para o ticker {} nao encontrado no Redis", ticker);
            return Optional.empty();
        }

        // Converte o objeto genérico do Redis para o nosso DTO
        return Optional.of(new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .convertValue(data, MarketDataDTO.class));
    }
}