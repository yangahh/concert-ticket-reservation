package kr.hhplus.be.server.interfaces.api.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Seat ID cannot be null")
    private Long seatId;
}
