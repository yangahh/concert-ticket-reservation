package kr.hhplus.be.server.infrastructure.concert.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatJpaRepository extends JpaRepository<Seat, Long> {

    @EntityGraph(attributePaths = {"concertSchedule"}, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT s FROM Seat s " +
        "JOIN s.concertSchedule cs " +
        "JOIN cs.concert c " +
        "WHERE c.id = :concertId " +
        "AND cs.eventDate BETWEEN :startDateTime AND :endDateTime")
    Page<Seat> findAllByConcertIdAndEventDate(Long concertId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
        @QueryHint(name = "javax.persistence.lock.timeout", value = "5000")  // lock timeout 5초
    })
    @Query("SELECT s FROM Seat s WHERE s.id = :seatId")
    Optional<Seat> findByIdWithLock(Long seatId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Seat s " +
        "SET s.isAvailable = true, s.tempReservationExpiredAt = null " +
        "WHERE s.id IN :seatIds")
    void updateByIdsToAvailable(List<Long> seatIds);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Seat s " +
        "SET s.isAvailable = true, s.tempReservationExpiredAt = null " +
        "WHERE s.id = :seatId")
    void updateToAvailableById(Long seatId);
}
