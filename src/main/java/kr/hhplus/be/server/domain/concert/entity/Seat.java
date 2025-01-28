package kr.hhplus.be.server.domain.concert.entity;

import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_schedule_id", nullable = false)
    private ConcertSchedule concertSchedule;

    @Column(name = "seat_no", nullable = false, length = 255)
    private String seatNo;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "temp_reservation_expired_at", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime tempReservationExpiredAt;

    @Version
    @Column(name = "version", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long version = 0L;

    @Builder(access = AccessLevel.PACKAGE)
    Seat(Long id, ConcertSchedule concertSchedule, String seatNo, Boolean isAvailable, Integer price, LocalDateTime tempReservationExpiredAt) {
        this.id = id;
        this.concertSchedule = concertSchedule;
        this.seatNo = seatNo;
        this.isAvailable = isAvailable;
        this.price = price;
        this.tempReservationExpiredAt = tempReservationExpiredAt;
    }

    @Builder(access = AccessLevel.PACKAGE)
    Seat(ConcertSchedule concertSchedule, String seatNo, Boolean isAvailable, Integer price, LocalDateTime tempReservationExpiredAt) {
        this.concertSchedule = concertSchedule;
        this.seatNo = seatNo;
        this.isAvailable = isAvailable;
        this.price = price;
        this.tempReservationExpiredAt = tempReservationExpiredAt;
    }

    public static Seat getReferenceById(Long id) {
        return Seat.builder().id(id).build();
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
