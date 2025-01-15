package kr.hhplus.be.server.interfaces.interceptor.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.filter.CustomCachingRequestWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoggingInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestId = UUID.randomUUID().toString();
        request.setAttribute("X-Request-ID", requestId);
        response.addHeader("X-Request-ID", requestId);

        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        log.trace("=======Request Logging=======");
        log.info("Request ID: [{}]", requestId);
        log.info("Request IP: [{}]", request.getRemoteAddr());
        log.info("Request Headers: {}", getRequestHeaders(request));
        log.info("Request Method and URL: [{}] {}", request.getMethod(), request.getRequestURI());

        // Request Param가 있을 경우 로깅
        if (request.getParameterNames().hasMoreElements()) {
            log.info("Request Params: {}", getRequestParams(request));
        }

        // Request Body가 있을 경우 로깅
        if (request instanceof CustomCachingRequestWrapper) {
            CustomCachingRequestWrapper requestWrapper = (CustomCachingRequestWrapper) request;
            String requestBody = new String(requestWrapper.getRequestBody());

            if (!requestBody.isEmpty()) {
                log.info("Request Body: {}", objectMapper.readTree(requestBody));
            }
        }
        log.trace("====End of Request Logging====");

        return true;
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headerMap.put(headerName, request.getHeader(headerName));
        }

        return headerMap;
    }

    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            paramMap.put(paramName, request.getParameter(paramName));
        }

        return paramMap;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        if (response instanceof ContentCachingResponseWrapper cachingResponse) {
            String requestId = (String) request.getAttribute("X-Request-ID");
            long startTime = (Long) request.getAttribute("startTime");
            long duration = System.currentTimeMillis() - startTime;

            log.trace("=======Response Logging=======");
            log.info("Request ID: [{}]", requestId);
            log.info("Response Time: {} ms", duration);

            if (isJsonContent(cachingResponse.getContentType())) {
                byte[] content = cachingResponse.getContentAsByteArray();
                if (content.length == 0) {
                    log.info("Response Status: [{}], URL: {}", response.getStatus(), request.getRequestURI());
                    log.info("Response Body: No Content");
                }
                if (content.length > 0) {
                    String responseBody = new String(content, StandardCharsets.UTF_8);
                    log.info("Response Status: [{}], URL: {}", response.getStatus(), request.getRequestURI());
                    log.info("Response Body: {}", objectMapper.readTree(responseBody));
                }
            }
            log.trace("====End of Response Logging====");
        }
    }

    private boolean isJsonContent(String contentType) {
        return contentType != null && contentType.contains("application/json");
    }

}
