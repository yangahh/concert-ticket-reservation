package kr.hhplus.be.server.infrastructure.queuetoken.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QueueTokenJpaRepository extends JpaRepository<QueueToken, Long> {
    Optional<QueueToken> findByTokenUuid(UUID tokenUuid);

    @Modifying
    void deleteByExpiredAtBefore(LocalDateTime datetime);

    @Query("SELECT COUNT(q) FROM QueueToken q WHERE q.isActive = :isActive")
    int countByIsActive(boolean isActive);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT qt.id FROM QueueToken qt WHERE qt.isActive = false ORDER BY qt.createdAt ASC")
    List<Long> findOldestWaitingTokenIds(Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE QueueToken qt SET qt.isActive = true WHERE qt.id IN :ids")
    void updateOldestWaitingTokensToActive(@Param("ids") List<Long> ids);

    @Query("SELECT COUNT(qt) " +
            "FROM QueueToken qt " +
            "WHERE qt.concertId = :concertId " +
            "AND qt.createdAt < :referenceCreatedAt " +
            "AND qt.expiredAt > :now " +
            "AND qt.isActive = false")
    int countWaitingTokensBeforeRefTime(@Param("concertId") Long concertId,
                           @Param("referenceCreatedAt") LocalDateTime referenceCreatedAt,
                           @Param("now") LocalDateTime now);


}

