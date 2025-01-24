package kr.hhplus.be.server.infrastructure.reservation.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT DISTINCT r.seat.id " +
        "FROM Reservation r " +
        "WHERE r.tempReservationExpiredAt < :now " +
        "AND r.status = 'PENDING_PAYMENT'")
    List<Long> findDistinctSeatIdsByExpiredReservation(LocalDateTime now);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Reservation r SET r.status = 'CANCELED' WHERE r.tempReservationExpiredAt < :now AND r.status = 'PENDING_PAYMENT'")
    void updatePendingPaymentReservationsToCanceled(LocalDateTime now);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT r FROM Reservation r WHERE r.id = :reservationId")
    Optional<Reservation> findByIdWithLock(Long reservationId);

}
