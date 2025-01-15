package kr.hhplus.be.server.domain.concert.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "concert_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @NotNull
    @Column(name = "event_date", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime eventDate;

    @Column(name = "total_seat_count", nullable = false)
    private Integer totalSeatCount = 0;

    @Builder
    private ConcertSchedule(Concert concert, LocalDateTime eventDate, Integer totalSeatCount) {
        this.concert = concert;
        this.eventDate = eventDate;
        this.totalSeatCount = totalSeatCount;
    }

    public static ConcertSchedule create(Concert concert, LocalDateTime eventDate, Integer totalSeatCount) {
        return ConcertSchedule.builder()
                .concert(concert)
                .eventDate(eventDate)
                .totalSeatCount(totalSeatCount)
                .build();
    }
}
