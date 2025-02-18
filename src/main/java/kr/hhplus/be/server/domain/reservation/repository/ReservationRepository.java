package kr.hhplus.be.server.domain.reservation.repository;

import kr.hhplus.be.server.domain.reservation.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    List<Long> findExpiredReservationSeatIds(LocalDateTime now);
    void updateExpiredTempReservationsToCanceled(LocalDateTime now);
    Reservation save(Reservation reservation);
    Optional<Reservation> findByIdForUpdate(Long reservationId);
    Optional<Reservation> findById(Long reservationId);
}
