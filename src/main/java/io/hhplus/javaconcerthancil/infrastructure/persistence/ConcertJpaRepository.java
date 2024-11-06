package io.hhplus.javaconcerthancil.infrastructure.persistence;

import io.hhplus.javaconcerthancil.domain.concert.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
}
