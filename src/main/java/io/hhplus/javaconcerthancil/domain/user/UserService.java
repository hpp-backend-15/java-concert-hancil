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

    public User findById(long userId) {
        return userRepository.findById(userId);
    }

    public UserWithVersion findByIdWithVersion(long userId) {
        return userWithVersionRepository.findById(userId);
    }

    public User findByIdWithLock(long userId) {
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
