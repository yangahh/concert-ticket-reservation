package kr.hhplus.be.server.infrastructure.repository.point;

import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.entity.PointHistory;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {
    private final PointJpaRepository pointJpaRepository;
    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    public Optional<Point> findByUserId(Long userId) {
        return pointJpaRepository.findByUserIdWithLock(userId);
    }

    @Override
    public Point savePoint(Point point) {
        return pointJpaRepository.save(point);
    }

    @Override
    public PointHistory savePointHistory(PointHistory history) {
        return pointHistoryJpaRepository.save(history);
    }

}
