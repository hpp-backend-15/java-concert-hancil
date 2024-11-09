package io.hhplus.javaconcerthancil.domain.user;

import java.util.Optional;

public interface UserRepository  {

    User findByIdWithLock(Long userId);
    User findById(long userId);
    User save(User user);
}
