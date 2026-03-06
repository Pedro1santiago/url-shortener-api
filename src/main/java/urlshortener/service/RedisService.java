package urlshortener.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void incrementClicks(String code) {
        redisTemplate.opsForValue().increment("clicks:" + code);
    }

    public Long getClicks(String code) {
        String value = redisTemplate.opsForValue().get("clicks:" + code);
        return value == null ? 0L : Long.parseLong(value);
    }

    public void cacheUrl(String code, String url) {
        redisTemplate.opsForValue().set("url:" + code, url);
    }

    public String getCachedUrl(String code) {
        return redisTemplate.opsForValue().get("url:" + code);
    }

    public boolean isAllowed(String ip) {

        String key = "ratelimit:" + ip;

        Long requests = redisTemplate.opsForValue().increment(key);

        if (requests == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }

        return requests <= 10;
    }
}