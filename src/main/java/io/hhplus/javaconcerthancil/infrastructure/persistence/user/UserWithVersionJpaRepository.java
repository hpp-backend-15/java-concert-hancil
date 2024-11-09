package io.hhplus.javaconcerthancil.infrastructure.persistence.user;

import io.hhplus.javaconcerthancil.domain.user.UserWithVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWithVersionJpaRepository extends JpaRepository<UserWithVersion, Long> {
}
