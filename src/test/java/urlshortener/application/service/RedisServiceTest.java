package urlshortener.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RedisService redisService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        redisService = new RedisService(redisTemplate);
    }

    @Test
    void incrementClicksShouldIncrementKey() {
        redisService.incrementClicks("abc");
        verify(valueOperations).increment("clicks:abc");
    }

    @Test
    void getClicksShouldReturnZeroWhenMissing() {
        when(valueOperations.get("clicks:abc")).thenReturn(null);
        assertEquals(0L, redisService.getClicks("abc"));
    }

    @Test
    void getClicksShouldParseLongValue() {
        when(valueOperations.get("clicks:abc")).thenReturn("12");
        assertEquals(12L, redisService.getClicks("abc"));
    }

    @Test
    void cacheUrlShouldStoreUrlByKey() {
        redisService.cacheUrl("abc", "https://example.com");
        verify(valueOperations).set("url:abc", "https://example.com");
    }

    @Test
    void getCachedUrlShouldReturnUrl() {
        when(valueOperations.get("url:abc")).thenReturn("https://example.com");
        assertEquals("https://example.com", redisService.getCachedUrl("abc"));
    }

    @Test
    void isAllowedShouldExpireOnFirstRequestAndBlockAfter10() {
        when(valueOperations.increment("ratelimit:1.2.3.4"))
                .thenReturn(1L)
                .thenReturn(10L)
                .thenReturn(11L);

        InOrder order = inOrder(valueOperations, redisTemplate);

        assertEquals(true, redisService.isAllowed("1.2.3.4"));
        order.verify(valueOperations).increment("ratelimit:1.2.3.4");
        order.verify(redisTemplate).expire("ratelimit:1.2.3.4", Duration.ofMinutes(1));

        assertEquals(true, redisService.isAllowed("1.2.3.4"));
        order.verify(valueOperations).increment("ratelimit:1.2.3.4");

        assertEquals(false, redisService.isAllowed("1.2.3.4"));
        order.verify(valueOperations).increment("ratelimit:1.2.3.4");
    }
}
