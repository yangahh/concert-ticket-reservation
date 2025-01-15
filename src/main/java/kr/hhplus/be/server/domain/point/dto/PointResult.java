package kr.hhplus.be.server.domain.point.dto;

import kr.hhplus.be.server.domain.point.entity.Point;
import lombok.Builder;

@Builder
public record PointResult(
        Long userId,
        Long pointId,
        int balance
) {
    public static PointResult fromEntity(Point point) {
        return PointResult.builder()
                .userId(point.getUser().getId())
                .pointId(point.getId())
                .balance(point.getBalance())
                .build();
    }
}
