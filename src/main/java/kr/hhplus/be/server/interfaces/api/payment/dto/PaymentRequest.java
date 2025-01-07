package kr.hhplus.be.server.interfaces.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    @Schema(description = "예약 ID", example = "1001")
    @NotNull(message = "Reservation Id cannot be null")
    @Positive
    private Long reservationId;
}
