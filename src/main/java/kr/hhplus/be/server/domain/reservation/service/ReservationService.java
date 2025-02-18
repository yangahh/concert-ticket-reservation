package kr.hhplus.be.server.domain.reservation.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.common.exception.UnprocessableEntityException;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.repository.ConcertRepository;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.event.ReservationExpiredEvent;
import kr.hhplus.be.server.domain.reservation.repository.ReservationRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ConcertRepository concertRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public List<Long> cancelExpiredTempReservations(LocalDateTime now) {
        List<Long> expiredReservationSeatIds = reservationRepository.findExpiredReservationSeatIds(now);
        reservationRepository.updateExpiredTempReservationsToCanceled(now);

        return expiredReservationSeatIds;
    }

    @Transactional
    public ReservationResult makeTempReservation(Long userId, Long seatId, LocalDateTime tempReservationExpiredAt) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found (id = " + userId + ")"));
        Seat seat = concertRepository.getReferenceSeatById(seatId);

        Reservation reservation = Reservation.tempReserve(user, seat, tempReservationExpiredAt);
        Reservation saved = reservationRepository.save(reservation);
        return ReservationResult.fromEntity(saved);
    }

    @Transactional
    public ReservationResult confirmReservation(Long reservationId, LocalDateTime now) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new EntityNotFoundException("Reservation not found (id = " + reservationId + ")"));
        validateReservation(reservation, now);

        reservation.confirm(now);
        Reservation saved = reservationRepository.save(reservation);
        return ReservationResult.fromEntity(saved);
    }

    private void validateReservation(Reservation reservation, LocalDateTime now) {
        boolean tempReservationExpired = reservation.isTempReservationExpired(now);

        if (tempReservationExpired) {
            applicationEventPublisher.publishEvent(new ReservationExpiredEvent(reservation.getId()));
            throw new UnprocessableEntityException("Temp reservation expired");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handelExpiredReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new EntityNotFoundException("Reservation not found (id = " + reservationId + ")"));

        concertRepository.updateSeatToAvailableById(reservation.getSeat().getId());
        reservation.cancel();
        reservationRepository.save(reservation);

        log.warn("Temp reservation expired. reservationId: {}", reservation.getId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void rollbackToTempReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new EntityNotFoundException("Reservation not found (id = " + reservationId + ")"));
        reservation.rollbackToTempReservation();
        reservationRepository.save(reservation);
    }
}
