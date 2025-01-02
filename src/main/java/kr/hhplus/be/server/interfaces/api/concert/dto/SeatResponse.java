package kr.hhplus.be.server.interfaces.api.concert.dto;


import kr.hhplus.be.server.domain.concert.vo.SeatStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatResponse {
    private long seatId;
    private String seatNo;
    private SeatStatus seatStatus;

    public static SeatResponse of(long seatId, String seatNo, SeatStatus seatStatus) {
        return SeatResponse.builder()
                .seatId(seatId)
                .seatNo(seatNo)
                .seatStatus(seatStatus)
                .build();
    }
}
