package kr.hhplus.be.server.domain.reservation.entity;

import jakarta.persistence.*;
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
@Table(name = "reservation",
    indexes = {
    @Index(name = "idx_reservation_seat_id", columnList = "seat_id"),
})
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING_PAYMENT;

    @Column(name = "payment_price", nullable = false)
    private int paymentPrice;

    @Column(name = "temp_reservation_expired_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime tempReservationExpiredAt;

    @Column(name = "confirmed_at", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime confirmedAt;

    @Version
    @Column(name = "version", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long version = 0L;

    @Builder(access = AccessLevel.PROTECTED)
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
        if (this.status == ReservationStatus.CONFIRMED) {
            throw new UnprocessableEntityException("Reservation is already confirmed. (id: " + id + ")");
        }
        this.status = ReservationStatus.CONFIRMED;
        this.confirmedAt = confirmedAt;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELED;
    }

    public boolean isTempReservationExpired(LocalDateTime now) {
        if (status != ReservationStatus.PENDING_PAYMENT) {
            throw new UnprocessableEntityException("Reservation is not a temporary reservation. (id: " + id + ")");
        }
        return tempReservationExpiredAt.isBefore(now);
    }

    public void rollbackToTempReservation() {
        this.status = ReservationStatus.PENDING_PAYMENT;
        this.confirmedAt = null;
    }
}
