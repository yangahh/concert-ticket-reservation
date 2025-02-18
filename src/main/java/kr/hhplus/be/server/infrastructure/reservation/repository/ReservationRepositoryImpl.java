package kr.hhplus.be.server.infrastructure.reservation.repository;

import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {
    private final ReservationJpaRepository reservationJpaRepository;

    public List<Long> findExpiredReservationSeatIds(LocalDateTime now) {
        return reservationJpaRepository.findDistinctSeatIdsByExpiredReservation(now);
    }

    @Override
    public void updateExpiredTempReservationsToCanceled(LocalDateTime now) {
        reservationJpaRepository.updatePendingPaymentReservationsToCanceled(now);
    }

    @Override
    public Reservation save(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public Optional<Reservation> findByIdForUpdate(Long reservationId) {
        return reservationJpaRepository.findByIdWithLock(reservationId);
    }

    @Override
    public Optional<Reservation> findById(Long reservationId) {
        return reservationJpaRepository.findById(reservationId);
    }
}
