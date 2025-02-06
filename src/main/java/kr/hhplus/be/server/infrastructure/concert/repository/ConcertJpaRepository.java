package kr.hhplus.be.server.infrastructure.concert.repository;

import kr.hhplus.be.server.domain.concert.entity.Concert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {

    @Query("SELECT c FROM Concert c WHERE c.reservationOpenDateTime >= :reservationOpenDateTime ORDER BY c.reservationOpenDateTime ASC")
    Page<Concert> findAllByReservationOpenDateTimeAfter(LocalDateTime reservationOpenDateTime, Pageable pageable);
}
