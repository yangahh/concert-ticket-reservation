package kr.hhplus.be.server.infrastructure.concert.repository;

import kr.hhplus.be.server.domain.concert.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
}
