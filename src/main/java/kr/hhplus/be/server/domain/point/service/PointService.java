package kr.hhplus.be.server.domain.point.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.point.dto.PointResult;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.entity.PointHistory;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import kr.hhplus.be.server.domain.point.vo.TransactionType;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    @Transactional
    public PointResult getUserPoint(Long userId) {
        Point userPoint = pointRepository.findByUserId(userId).orElse(initializePoint(userId));
        return PointResult.fromEntity(userPoint);
    }

    private Point initializePoint(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return Point.create(user);
    }

    @Transactional
    public PointResult chargePoint(Long userId, int amount) {
        Point userPoint = pointRepository.findByUserId(userId).orElse(initializePoint(userId));
        userPoint.plus(amount);
        Point updated = pointRepository.savePoint(userPoint);

        recordPointHistory(updated, amount, TransactionType.CHARGE, null);
        return PointResult.fromEntity(updated);
    }

    @Transactional
    public PointResult usePoint(Long userId, int amount, Long refReservationId) {
        Point userPoint = pointRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException("Point not found"));
        userPoint.minus(amount);
        Point updated = pointRepository.savePoint(userPoint);

        recordPointHistory(updated, amount, TransactionType.USE, refReservationId);
        return PointResult.fromEntity(updated);
    }

    private void recordPointHistory(Point point, int amount, TransactionType type, Long refReservationId) {
        PointHistory history = PointHistory.create(point, amount, type, refReservationId);
        pointRepository.savePointHistory(history);
    }

}
