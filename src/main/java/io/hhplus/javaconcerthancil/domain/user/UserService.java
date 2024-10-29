package io.hhplus.javaconcerthancil.domain.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserWithVersionRepository userWithVersionRepository;

    public Optional<User> findById(long userId) {
        return userRepository.findById(userId);
    }

    public Optional<UserWithVersion> findByIdWithVersion(long userId) {
        return userWithVersionRepository.findById(userId);
    }


    @Transactional
    public Optional<User> findByIdWithLock(long userId) {
        return userRepository.findByIdWithLock(userId);
    }


    public User updateBalance(User user) {
        return userRepository.save(user);
    }

    public UserWithVersion updateBalanceWithVersion(UserWithVersion user) {
        return userWithVersionRepository.save(user);
    }

//    public BalanceHistory saveHistory(BalanceHistory balanceHistory) {
//        return balanceHistoryRepository.save(balanceHistory);
//    }
}
