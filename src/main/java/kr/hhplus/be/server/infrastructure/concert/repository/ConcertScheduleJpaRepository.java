package kr.hhplus.be.server.infrastructure.concert.repository;

import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertSchedule, Long> {
    Optional<ConcertSchedule> findById(Long id);

    @EntityGraph(attributePaths = {"concert"}, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT cs FROM ConcertSchedule cs WHERE cs.concert.id = :concertId")
    Page<ConcertSchedule> findAllByConcertId(Long concertId, Pageable pageable);
}