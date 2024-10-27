package io.hhplus.javaconcerthancil.domain.user;

import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.boot.logging.LogLevel;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "concert_user")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private long balance;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BalanceHistory> balanceHistoryList = new ArrayList<>();

    public User(String name) {
        this.name = name;
        this.balance = 0;
    }

    public User(String name, long balance) {
        this.name = name;
        this.balance = balance;
    }

    public void addAmount(int amount) {
        if(amount <= 0){
            throw new ApiException(ErrorCode.E006, LogLevel.INFO, "amount must be positive.");
        }
        this.balance += amount;
        recordBalanceHistory(amount, TransactionType.CHARGE); // 충전 기록
    }

    public void subtractAmount(int amount) {
        if (this.balance < amount) {
            throw new ApiException(ErrorCode.E005, LogLevel.INFO, "userAmount= " + amount + " this.amount= " + this.balance);
        }
        this.balance -= amount;
        recordBalanceHistory(amount, TransactionType.USE); // 충전 기록
    }

    private void recordBalanceHistory(int amount, TransactionType transactionType) {
        BalanceHistory history = new BalanceHistory(this, amount, transactionType);
        balanceHistoryList.add(history);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getBalance() {
        return balance;
    }

    public List<BalanceHistory> getBalanceHistoryList() {
        return balanceHistoryList;
    }
}
