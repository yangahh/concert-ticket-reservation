package kr.hhplus.be.server.domain.reservation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import kr.hhplus.be.server.domain.common.exception.UnprocessableEntityException;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import kr.hhplus.be.server.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservation")
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING_PAYMENT;

    @NotNull
    @Column(name = "payment_price", nullable = false)
    private int paymentPrice;

    @NotNull
    @Column(name = "temp_reservation_expired_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime tempReservationExpiredAt;

    @Column(name = "confirmed_at", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime confirmedAt;

    @Builder
    Reservation(User user, Seat seat, ReservationStatus status, int paymentPrice, LocalDateTime tempReservationExpiredAt, LocalDateTime confirmedAt) {
        this.user = user;
        this.seat = seat;
        this.status = status;
        this.paymentPrice = paymentPrice;
        this.tempReservationExpiredAt = tempReservationExpiredAt;
        this.confirmedAt = confirmedAt;
    }

    public static Reservation tempReserve(User user, Seat seat, LocalDateTime tempReservationExpiredAt) {
        return Reservation.builder()
            .user(user)
            .seat(seat)
            .status(ReservationStatus.PENDING_PAYMENT)
            .paymentPrice(seat.getPrice())
            .tempReservationExpiredAt(tempReservationExpiredAt)
            .build();
    }

    public void confirm(LocalDateTime confirmedAt) {
        this.status = ReservationStatus.CONFIRMED;
        this.confirmedAt = confirmedAt;
    }

    public boolean isTempReservationExpired(LocalDateTime now) {
        if (status != ReservationStatus.PENDING_PAYMENT) {
            throw new UnprocessableEntityException("The reservation is not a temporary reservation");
        }
        return tempReservationExpiredAt.isBefore(now);
    }
}
