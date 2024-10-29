package io.hhplus.javaconcerthancil.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWithVersionRepository extends JpaRepository<UserWithVersion, Long> {

}
