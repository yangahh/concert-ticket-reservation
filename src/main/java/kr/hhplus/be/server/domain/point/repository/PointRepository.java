package kr.hhplus.be.server.domain.point.repository;

import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.entity.PointHistory;

import java.util.Optional;

public interface PointRepository {
    Optional<Point> findByUserId(Long userId);
    Point savePoint(Point point);
    PointHistory savePointHistory(PointHistory history);
}
