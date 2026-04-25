package com.rvneto.b3.matching.engine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rvneto.b3.matching.engine.dto.MarketDataDTO;
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
    private final ObjectMapper objectMapper;
    private static final String CACHE_KEY_PREFIX = "market:price:";

    public MarketPriceService(@Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate,
                               @Qualifier("objectMapper") ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public Optional<MarketDataDTO> getCurrentPrice(String ticker) {

        String key = CACHE_KEY_PREFIX + ticker.toUpperCase().trim();

        log.info("Looking up key: [{}]", key);

        Object data = redisTemplate.opsForValue().get(key);

        if (isNull(data)) {
            log.warn("Price for ticker {} not found in Redis", ticker);
            return Optional.empty();
        }

        // Converts the generic Redis object to our DTO
        return Optional.of(objectMapper.convertValue(data, MarketDataDTO.class));
    }
}