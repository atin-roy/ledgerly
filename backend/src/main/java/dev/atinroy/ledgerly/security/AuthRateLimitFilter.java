package dev.atinroy.ledgerly.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fixed-window rate limit for the unauthenticated auth endpoints, keyed by
 * client IP. Guards login/register/refresh against credential stuffing;
 * everything behind a JWT is left to normal auth.
 */
@Slf4j
@Component
public class AuthRateLimitFilter extends OncePerRequestFilter {

    private static final long WINDOW_MILLIS = 60_000;

    @Value("${auth.rate-limit.requests-per-minute:15}")
    private int requestsPerMinute;

    /* trust X-Forwarded-For only when running behind the reverse proxy */
    @Value("${auth.rate-limit.trust-forwarded-for:false}")
    private boolean trustForwardedFor;

    private record Window(long startMillis, AtomicInteger count) {}

    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        long now = System.currentTimeMillis();
        String key = clientKey(request);

        Window window = windows.compute(key, (k, w) ->
                (w == null || now - w.startMillis() >= WINDOW_MILLIS)
                        ? new Window(now, new AtomicInteger())
                        : w
        );

        if (window.count().incrementAndGet() > requestsPerMinute) {
            log.warn("Rate limit exceeded on {} {} from {}", request.getMethod(), request.getRequestURI(), key);
            response.setStatus(429);
            response.setContentType("application/json");
            response.setHeader("Retry-After", "60");
            response.getWriter().write(
                    "{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Too many attempts. Try again in a minute.\",\"path\":\""
                    + request.getRequestURI() + "\"}"
            );
            return;
        }

        if (windows.size() > 10_000) {
            windows.entrySet().removeIf(e -> now - e.getValue().startMillis() >= WINDOW_MILLIS);
        }

        filterChain.doFilter(request, response);
    }

    private String clientKey(HttpServletRequest request) {
        if (trustForwardedFor) {
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                return forwarded.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().startsWith("/api/auth/");
    }
}
