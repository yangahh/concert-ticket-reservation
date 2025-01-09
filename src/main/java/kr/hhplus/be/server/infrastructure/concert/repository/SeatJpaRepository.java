package kr.hhplus.be.server.infrastructure.concert.repository;

import kr.hhplus.be.server.domain.concert.entity.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface SeatJpaRepository extends JpaRepository<Seat, Long> {
    Optional<Seat> findById(Long id);

    @EntityGraph(attributePaths = {"concertSchedule"}, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT s FROM Seat s WHERE s.concertSchedule.concert.id = :concertId AND s.concertSchedule.eventDate = :eventDate")
    Page<Seat> findAllByConcertIdAndEventDate(Long concertId, LocalDateTime eventDate, Pageable pageable);
}
