package kr.hhplus.be.server.domain.concert.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import kr.hhplus.be.server.domain.common.exception.UnprocessableEntityException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "seat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat extends BaseEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_schedule_id", nullable = false)
    private ConcertSchedule concertSchedule;

    @NotNull
    @Column(name = "seat_no", nullable = false, length = 255)
    private String seatNo;

    @NotNull
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @NotNull
    @Column(nullable = false)
    private Integer price;

    @Column(name = "temp_reservation_expired_at", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime tempReservationExpiredAt;

    @Builder(access = AccessLevel.PROTECTED)
    Seat(ConcertSchedule concertSchedule, String seatNo, Boolean isAvailable, Integer price, LocalDateTime tempReservationExpiredAt) {
        this.concertSchedule = concertSchedule;
        this.seatNo = seatNo;
        this.isAvailable = isAvailable;
        this.price = price;
        this.tempReservationExpiredAt = tempReservationExpiredAt;
    }

    public static Seat create(ConcertSchedule concertSchedule, String seatNo, boolean isAvailable, Integer price, LocalDateTime tempReservationExpiredAt) {
        return Seat.builder()
                .concertSchedule(concertSchedule)
                .seatNo(seatNo)
                .isAvailable(isAvailable)
                .price(price)
                .tempReservationExpiredAt(tempReservationExpiredAt)
                .build();
    }

    public boolean isAvailableNow(LocalDateTime now) {
        if (this.tempReservationExpiredAt != null && this.tempReservationExpiredAt.isBefore(now)) {
            this.isAvailable = true;
            this.tempReservationExpiredAt = null;
            return true;
        }

        return this.isAvailable;
    }

    public void reserve(LocalDateTime now) {
        if (!this.isAvailableNow(now)) {
            throw new UnprocessableEntityException("Seat is already reserved (id = " + this.id + ")");
        }

        this.isAvailable = false;
        this.tempReservationExpiredAt = now.plusMinutes(5);
    }
}
