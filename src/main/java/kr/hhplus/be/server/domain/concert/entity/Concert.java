package kr.hhplus.be.server.domain.concert.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "concert")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime reservationOpenDateTime;

    Concert(String title, LocalDateTime reservationOpenDateTime) {
        this.title = title;
        this.reservationOpenDateTime = reservationOpenDateTime;
    }

    public static Concert create(String title, LocalDateTime reservationOpenDateTime) {
        return new Concert(title, reservationOpenDateTime);
    }
}
