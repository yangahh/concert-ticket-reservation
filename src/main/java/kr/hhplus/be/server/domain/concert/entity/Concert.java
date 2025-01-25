package kr.hhplus.be.server.domain.concert.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "concert")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    Concert(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    Concert(String title) {
        this.title = title;
    }

    public static Concert create(String title) {
        return new Concert(title);
    }
}
