package kr.hhplus.be.server.interfaces.api.concert.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatResponse {
    private long seatId;

    @Schema(description = "좌석 번호", example = "A1")
    private String seatNo;

    @Schema(description = "좌석 예약 가능 여부")
    private boolean isAvailable;

    public static SeatResponse of(long seatId, String seatNo, boolean isAvailable) {
        return SeatResponse.builder()
                .seatId(seatId)
                .seatNo(seatNo)
                .isAvailable(isAvailable)
                .build();
    }
}
