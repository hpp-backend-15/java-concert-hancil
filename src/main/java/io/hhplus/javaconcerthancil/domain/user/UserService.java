package io.hhplus.javaconcerthancil.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;

    public Optional<User> findById(long userId) {
        return userRepository.findById(userId);
    }

    public User updateBalance(User user) {
        return userRepository.save(user);
    }

    public BalanceHistory saveHistory(BalanceHistory balanceHistory) {
        return balanceHistoryRepository.save(balanceHistory);
    }
}
