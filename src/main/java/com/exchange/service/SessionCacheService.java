package com.exchange.service;

import com.exchange.model.entity.ExchangeSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SessionCacheService {

    private static final String SESSION_CACHE_PREFIX = "session:";
    private static final long CACHE_TTL_MINUTES = 60;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public SessionCacheService(RedisTemplate<String, String> redisTemplate,
                             ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void saveSession(ExchangeSession session) {
        try {
            String key = SESSION_CACHE_PREFIX + session.getId();
            String value = objectMapper.writeValueAsString(session);
            redisTemplate.opsForValue().set(key, value, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("Session cached: {}", session.getId());
        } catch (Exception e) {
            log.error("Error caching session", e);
        }
    }

    public ExchangeSession getSession(String sessionId) {
        try {
            String key = SESSION_CACHE_PREFIX + sessionId;
            String value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                log.debug("Session retrieved from cache: {}", sessionId);
                return objectMapper.readValue(value, ExchangeSession.class);
            }
        } catch (Exception e) {
            log.error("Error retrieving session from cache", e);
        }
        return null;
    }

    public void invalidateSession(String sessionId) {
        String key = SESSION_CACHE_PREFIX + sessionId;
        redisTemplate.delete(key);
        log.debug("Session cache invalidated: {}", sessionId);
    }
}
