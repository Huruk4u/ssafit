package com.example.ssafit.model.dto.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class TokenBlacklist {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public TokenBlacklist(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 시간이 지나면 만료되는 토큰을 template에 입력
    public void add(String token, long ttlSeconds) {
        redisTemplate.opsForValue().set(token, "blacklisted", ttlSeconds, TimeUnit.SECONDS);
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }
}
