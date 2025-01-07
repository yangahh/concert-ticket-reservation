package kr.hhplus.be.server.interfaces.api.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    @Schema(description = "사용자 ID", example = "1")
    @NotNull(message = "User ID cannot be null")
    @Positive
    private Long userId;

    @Schema(description = "좌석 ID", example = "31")
    @NotNull(message = "Seat ID cannot be null")
    @Positive
    private Long seatId;
}
