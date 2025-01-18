package kr.hhplus.be.server.config;

import kr.hhplus.be.server.interfaces.interceptor.common.LoggingInterceptor;
import kr.hhplus.be.server.interfaces.interceptor.queuetoken.QueueTokenValidationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final LoggingInterceptor loggingInterceptor;
    private final QueueTokenValidationInterceptor queueTokenValidationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
            .addPathPatterns("/**");

        registry.addInterceptor(queueTokenValidationInterceptor)
            .addPathPatterns("/reservations/**", "/payments/**", "/concerts/**");
    }
}
