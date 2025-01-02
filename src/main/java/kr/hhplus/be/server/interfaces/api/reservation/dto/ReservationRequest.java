package kr.hhplus.be.server.interfaces.api.reservation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    @NotNull(message = "User ID cannot be null")
    @Positive
    private Long userId;

    @NotNull(message = "Seat ID cannot be null")
    @Positive
    private Long seatId;
}
