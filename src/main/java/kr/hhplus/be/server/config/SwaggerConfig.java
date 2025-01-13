package kr.hhplus.be.server.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
        info = @Info(
                title = "Concert Reservation Service API",
                version = "1.0",
                description = "콘서트 예약 서비스 API 명세서"
        ),
        tags = {
                @Tag(name = "Queue", description = "대기열 관련 API"),
                @Tag(name = "Concert", description = "콘서트 관련 API"),
                @Tag(name = "Reservation", description = "예약 관련 API"),
                @Tag(name = "Payment", description = "결제 관련 API"),
                @Tag(name = "Point", description = "포인트 관련 API")
        }
)
public class SwaggerConfig {
}
