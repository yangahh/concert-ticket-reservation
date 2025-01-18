package kr.hhplus.be.server.interfaces.api.concert.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.concert.dto.ConcertSeatResult;
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

    @Schema(description = "가격")
    private int price;

    public static SeatResponse fromDomainDto(ConcertSeatResult dto) {
        return SeatResponse.builder()
                .seatId(dto.seatId())
                .seatNo(dto.seatNo())
                .isAvailable(dto.isAvailable())
                .price(dto.price())
                .build();
    }
}
