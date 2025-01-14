package kr.hhplus.be.server.domain.concert.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
