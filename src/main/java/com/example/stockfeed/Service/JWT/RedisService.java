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

    public void saveLastLogout(String username, long timestamp) {
       redisTemplate.opsForValue().set("lastLogout:" + username, String.valueOf(timestamp));
    } // 마지막 로그아웃 시간 저장

    public Long getLastLogout(String username) {
        String timestamp = redisTemplate.opsForValue().get("lastLogout:" + username);
        return timestamp != null ? Long.parseLong(timestamp) : null;
    } // 마지막 로그아웃 시간 가져오기

    public void deleteRefreshToken(String key) {
        redisTemplate.delete(key);
    }

    // 로그아웃한 사용자 토큰 블랙리스트에 저장
    public void saveBlacklist(String token, long duration) {
        redisTemplate.opsForValue().set(token, "blacklisted", duration, TimeUnit.MILLISECONDS);
    }

    // 블랙리스트에 있는지 확인
    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }

}
