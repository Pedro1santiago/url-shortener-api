package urlshortener.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import urlshortener.application.service.RedisService;
import org.springframework.lang.NonNull;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisService redisService;

    public RateLimitFilter(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String ip = getClientIp(request);

        if (!redisService.isAllowed(ip)) {

            response.setStatus(429);
            response.setContentType("text/plain");
            response.setHeader("Retry-After", "60");
            response.getWriter().write("Too many requests");

            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {

        String header = request.getHeader("X-Forwarded-For");

        if (header != null && !header.isBlank()) {
            return header.split(",")[0];
        }

        return request.getRemoteAddr();
    }
}