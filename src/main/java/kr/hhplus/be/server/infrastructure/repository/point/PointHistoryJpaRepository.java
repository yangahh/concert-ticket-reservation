package kr.hhplus.be.server.infrastructure.repository.point;

import kr.hhplus.be.server.domain.point.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointHistoryJpaRepository extends JpaRepository<PointHistory, Long> {
}
