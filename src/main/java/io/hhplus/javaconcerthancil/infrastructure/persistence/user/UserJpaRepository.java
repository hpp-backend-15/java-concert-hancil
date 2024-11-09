package io.hhplus.javaconcerthancil.infrastructure.persistence.user;

import io.hhplus.javaconcerthancil.domain.user.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM concert_user u WHERE u.id = :id")
    Optional<User> findByIdWithLock(@Param("id") Long userId);

}
