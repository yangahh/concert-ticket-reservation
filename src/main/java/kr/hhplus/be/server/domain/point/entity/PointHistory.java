package kr.hhplus.be.server.domain.point.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import kr.hhplus.be.server.domain.point.vo.TransactionType;
import kr.hhplus.be.server.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point_history",
    indexes = {
        @Index(name = "idx_point_history_ref_id", columnList = "ref_id"),
    })
public class PointHistory extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id", nullable = false)
    private Point point;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "balance", nullable = false)
    private Integer balance;

    @Column(name = "ref_id", nullable = false)
    private Long refId;

    @Builder(access = AccessLevel.PROTECTED)
    PointHistory(Point point, User user, TransactionType transactionType, int amount, int balance, Long refId) {
        this.user = user;
        this.point = point;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balance = balance;
        this.refId = refId;
    }

    public static PointHistory create(Point point, int amount, TransactionType transactionType, Long reservationId) {
        return PointHistory.builder()
                .user(point.getUser())
                .point(point)
                .transactionType(transactionType)
                .amount(amount)
                .balance(point.getBalance())
                .refId(reservationId)
                .build();
    }
}
