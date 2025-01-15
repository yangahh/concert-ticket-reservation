package kr.hhplus.be.server.interfaces.interceptor.queuetoken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.domain.queuetoken.exception.InvalidToken;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.utils.regexp.Patterns;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class QueueTokenValidationInterceptor implements HandlerInterceptor {
    private final QueueTokenService queueTokenService;
    private static final String QUEUE_TOKEN_HEADER = "token";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
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

}
