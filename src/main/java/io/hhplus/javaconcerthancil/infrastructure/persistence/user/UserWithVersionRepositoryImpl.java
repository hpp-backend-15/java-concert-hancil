package io.hhplus.javaconcerthancil.infrastructure.persistence.user;

import io.hhplus.javaconcerthancil.domain.user.UserWithVersion;
import io.hhplus.javaconcerthancil.domain.user.UserWithVersionRepository;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserWithVersionRepositoryImpl implements UserWithVersionRepository {

    private final UserWithVersionJpaRepository userWithVersionJpaRepository;
    @Override
    public UserWithVersion findById(long userId) {
        return userWithVersionJpaRepository.findById(userId).orElseThrow(
                () -> new ApiException(ErrorCode.E404, LogLevel.INFO, "User not found"));
    }

    @Override
    public UserWithVersion save(UserWithVersion user) {
        return userWithVersionJpaRepository.save(user);
    }
}
