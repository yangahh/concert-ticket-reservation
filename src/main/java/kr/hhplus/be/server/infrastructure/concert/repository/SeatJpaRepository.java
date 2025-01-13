package kr.hhplus.be.server.infrastructure.concert.repository;

import kr.hhplus.be.server.domain.concert.entity.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Repository
public interface SeatJpaRepository extends JpaRepository<Seat, Long> {

    @EntityGraph(attributePaths = {"concertSchedule"}, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT s FROM Seat s " +
        "JOIN s.concertSchedule cs " +
        "JOIN cs.concert c " +
        "WHERE c.id = :concertId " +
        "AND cs.eventDate BETWEEN :startDateTime AND :endDateTime")
    Page<Seat> findAllByConcertIdAndEventDate(Long concertId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
}
