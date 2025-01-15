package kr.hhplus.be.server.interfaces.interceptor.queuetoken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.domain.queuetoken.exception.InvalidToken;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.utils.regexp.Patterns;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;
import java.util.UUID;

@Component
public class QueueTokenValidationInterceptor implements HandlerInterceptor {
    private static final String QUEUE_TOKEN_HEADER = "X-Queue-Token";
    private final QueueTokenService queueTokenService;

    public QueueTokenValidationInterceptor(Optional<QueueTokenService> queueTokenService) {
        this.queueTokenService = queueTokenService.orElse(null);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 토큰 검증이 필요하지 않는 Controller 에 대한 테스트에서는 Interceptor 를 거치지 않도록 설정
        if (isTestEnvironment() && isNotUseQueueTokenValidationRequest(request)) return true;

        String queueToken = request.getHeader(QUEUE_TOKEN_HEADER);
        if (queueToken == null || queueToken.isEmpty()) {
            throw new IllegalArgumentException("Missing required header: " + QUEUE_TOKEN_HEADER);
        }

        if (!queueToken.matches(Patterns.UUID)) {
            throw new IllegalArgumentException("Invalid token format: " + queueToken);
        }

        boolean tokenValid = queueTokenService.isTokenValid(UUID.fromString(queueToken));
        if (!tokenValid) {
            throw new InvalidToken("Token is invalid");
        }
        return true;
    }

    private boolean isTestEnvironment() {
        return "test".equals(System.getProperty("spring.profiles.active", "default"));
    }

    private boolean isNotUseQueueTokenValidationRequest(HttpServletRequest request) {
        String[] useQueueTokenValidationRequest = {"/reservations", "/payments", "/concerts/"};
        for (String path : useQueueTokenValidationRequest) {
            if (request.getRequestURL().toString().contains(path)) {
                return false;
            }
        }
        return true;
    }

}
