package com.edigest.journalApp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> T get(String key, Class<T> entityClass) {
        try {
            Object o = redisTemplate.opsForValue().get(key);
            if (o == null) {
                log.info("No value found for key: {}", key);
                return null; // Or throw a custom exception if appropriate
            }
            String jsonString = o.toString(); // Safely convert to String if not null
            return objectMapper.readValue(jsonString, entityClass);
        } catch (Exception e) {
            log.error("Exception while retrieving value for key: {}", key, e);
            return null; // Or handle as needed
        }
    }

    public void set(String key, Object o, Long ttl) {
        try {
            String jsonValue = objectMapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key, jsonValue, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Exception while setting value for key: {}", key, e);
        }
    }
}
