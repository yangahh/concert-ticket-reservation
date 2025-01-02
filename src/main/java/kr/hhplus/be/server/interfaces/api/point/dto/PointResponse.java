package kr.hhplus.be.server.interfaces.api.point.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointResponse {
    private long userId;
    private Long balance;
}
