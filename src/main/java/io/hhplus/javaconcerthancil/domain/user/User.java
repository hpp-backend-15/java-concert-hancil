package io.hhplus.javaconcerthancil.domain.user;

import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.boot.logging.LogLevel;

import java.util.List;

@Entity(name = "concert_user")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private long balance;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<BalanceHistory> balanceHistoryList;

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
        this.balance = 0;
    }

    public User(String name) {
        this.name = name;
        this.balance = 0;
    }

    public void addAmount(int amount) {
        if(amount <= 0){
            throw new ApiException(ErrorCode.E006, LogLevel.INFO, "amount must be positive.");
        }
        this.balance += amount;
    }

    public void subtractAmount(int amount) {
        if (this.balance < amount) {
            throw new ApiException(ErrorCode.E005, LogLevel.INFO, "userAmount = " + amount + "this.amount = " + this.balance);
        }
        this.balance -= amount;
    }

    public long getBalance() {
        return balance;
    }
}
