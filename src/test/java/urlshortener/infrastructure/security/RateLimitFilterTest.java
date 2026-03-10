package urlshortener.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import urlshortener.application.service.RedisService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    @Mock
    private RedisService redisService;

    @Test
    void shouldReturn429WhenRateLimited() throws Exception {

        when(redisService.isAllowed("1.2.3.4")).thenReturn(false);

        RateLimitFilter filter = new RateLimitFilter(redisService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("1.2.3.4");

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals(429, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("60", response.getHeader("Retry-After"));
        assertEquals("Too many requests", response.getContentAsString());
        verify(redisService).isAllowed("1.2.3.4");
    }

    @Test
    void shouldUseFirstIpFromXForwardedForHeader() throws Exception {

        when(redisService.isAllowed("9.9.9.9")).thenReturn(true);

        RateLimitFilter filter = new RateLimitFilter(redisService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "9.9.9.9, 1.1.1.1");
        request.setRemoteAddr("1.2.3.4");

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        verify(redisService).isAllowed("9.9.9.9");
    }
}
