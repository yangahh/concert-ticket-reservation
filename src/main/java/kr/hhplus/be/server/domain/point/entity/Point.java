package kr.hhplus.be.server.domain.point.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import kr.hhplus.be.server.domain.common.exception.UnprocessableEntityException;
import kr.hhplus.be.server.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point")
public class Point extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "balance", nullable = false)
    private Integer balance;

    Point(User user, int balance) {
        this.user = user;
        this.balance = balance;
    }

    public static Point create(User user) {
        return new Point(user, 0);
    }

    public void plus(int amount) {
        this.balance += amount;
    }

    public void minus(int amount) {
        if (this.balance - amount < 0) {
            throw new UnprocessableEntityException("Not enough point for use");
        }
        this.balance -= amount;
    }

}
