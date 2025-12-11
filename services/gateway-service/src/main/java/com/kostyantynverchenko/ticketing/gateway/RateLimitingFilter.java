package com.kostyantynverchenko.ticketing.gateway;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${gateway.rate-limiter.enabled}")
    private boolean enabled;

    @Value("${gateway.rate-limiter.max-requests}")
    private long maxRequests;

    @Value("${gateway.rate-limiter.window-seconds}")
    private long windowSeconds;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientKey = resolveClientKey(request);

        try {
            String redisKey = "gateway:rate-limit:" + clientKey;
            Long requestCount = stringRedisTemplate.opsForValue().increment(redisKey);

            if (requestCount != null && requestCount == 1L) {
                stringRedisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds));
            }

            if (requestCount != null && requestCount > maxRequests) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Too many requests\"}");
                return;
            }
        } catch (Exception ex) {
            log.warn("Failed to apply rate limiting. Continuing request flow.", ex);
        }
    }

    private String resolveClientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return Optional.ofNullable(request.getRemoteAddr()).orElse("unknown");
    }
}
