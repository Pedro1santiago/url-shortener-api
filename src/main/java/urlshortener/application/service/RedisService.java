package urlshortener.application.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /** Increments the click counter for a short code. */
    public void incrementClicks(String code) {
        redisTemplate.opsForValue().increment("clicks:" + code);
    }

    /** Gets the click counter for a short code (defaults to zero). */
    public Long getClicks(String code) {
        String value = redisTemplate.opsForValue().get("clicks:" + code);
        return value == null ? 0L : Long.parseLong(value);
    }

    /** Caches the resolved original URL for a short code. */
    public void cacheUrl(String code, String url) {
        redisTemplate.opsForValue().set("url:" + code, url);
    }

    /** Returns the cached original URL for a short code (or null). */
    public String getCachedUrl(String code) {
        return redisTemplate.opsForValue().get("url:" + code);
    }

    /** Checks and updates per-IP rate limit state (10 requests per minute). */
    public boolean isAllowed(String ip) {

        String key = "ratelimit:" + ip;

        Long requests = redisTemplate.opsForValue().increment(key);

        if (requests == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }

        return requests <= 10;
    }
}