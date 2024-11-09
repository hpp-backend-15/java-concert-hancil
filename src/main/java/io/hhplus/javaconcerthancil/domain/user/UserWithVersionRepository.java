package io.hhplus.javaconcerthancil.domain.user;

public interface UserWithVersionRepository {

    UserWithVersion findById(long userId);
    UserWithVersion save(UserWithVersion user);
}
