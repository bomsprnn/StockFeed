package com.example.stockfeed.Service.JWT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveRefreshToken(String key, String value, long timeout) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(String key) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    public void deleteRefreshToken(String key) {
        redisTemplate.delete(key);
    }
}
