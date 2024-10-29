package io.hhplus.javaconcerthancil.domain.user;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class BalanceHistoryVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserWithVersion user;
    private long amount;
    private LocalDateTime transactionAt;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // charge, use etc..

    public BalanceHistoryVersion(UserWithVersion user, long amount, TransactionType type) {
        this.user = user;
        this.amount = amount;
        this.transactionAt = LocalDateTime.now();
        this.type = type;
    }

    public TransactionType getType() {
        return type;
    }

    public long getAmount() {
        return amount;
    }

    public UserWithVersion getUser() {
        return user;
    }
}

