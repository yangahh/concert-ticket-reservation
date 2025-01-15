package kr.hhplus.be.server.domain.reservation.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.repository.ConcertRepository;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.repository.ReservationRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ConcertRepository concertRepository;

    @Transactional
    public List<Long> cancelExpiredTempReservations(LocalDateTime now) {
        List<Long> expiredReservationSeatIds = reservationRepository.findExpiredReservationSeatIds(now);
        reservationRepository.updateExpiredTempReservationsToCanceled(now);
        return expiredReservationSeatIds;
    }

    @Transactional
    public ReservationResult makeTempReservation(Long userId, Long seatId, LocalDateTime tempReservationExpiredAt) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Seat seat = concertRepository.findSeatById(seatId)
            .orElseThrow(() -> new EntityNotFoundException("Seat not found"));

        Reservation reservation = Reservation.tempReserve(user, seat, tempReservationExpiredAt);
        Reservation saved = reservationRepository.save(reservation);
        return ReservationResult.fromEntity(saved);
    }

    private Reservation getReservation(Long reservationId) {
        return reservationRepository.findByIdForUpdate(reservationId)
            .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
    }

    @Transactional(readOnly = true)
    public boolean isTempReservationExpired(Long reservationId, LocalDateTime now) {
        Reservation reservation = getReservation(reservationId);
        return reservation.isTempReservationExpired(now);
    }

    @Transactional
    public ReservationResult confirmReservation(Long reservationId, LocalDateTime now) {
        Reservation reservation = reservationRepository.findByIdForUpdate(reservationId)
            .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
        reservation.confirm(now);
        Reservation saved = reservationRepository.save(reservation);
        return ReservationResult.fromEntity(saved);
    }
}
