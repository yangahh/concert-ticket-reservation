package kr.hhplus.be.server.infrastructure.point.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import kr.hhplus.be.server.domain.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointJpaRepository extends JpaRepository<Point, Long> {
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT p FROM Point p WHERE p.user.id = :userId")
    Optional<Point> findByUserIdWithLock(Long userId);
}
