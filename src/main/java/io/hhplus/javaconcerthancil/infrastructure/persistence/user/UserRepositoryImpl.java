package io.hhplus.javaconcerthancil.infrastructure.persistence.user;

import io.hhplus.javaconcerthancil.domain.user.User;
import io.hhplus.javaconcerthancil.domain.user.UserRepository;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    @Override
    public User findByIdWithLock(Long userId) {
        return userJpaRepository.findByIdWithLock(userId).orElseThrow(
                () -> new ApiException(ErrorCode.E404, LogLevel.INFO, "User not found")
        );
    }

    @Override
    public User findById(long userId) {
        return userJpaRepository.findById(userId).orElseThrow(
                () -> new ApiException(ErrorCode.E404, LogLevel.INFO, "User not found")
        );
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }
}
