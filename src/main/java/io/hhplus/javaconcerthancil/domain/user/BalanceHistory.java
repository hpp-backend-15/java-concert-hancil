package io.hhplus.javaconcerthancil.domain.user;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class BalanceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private long amount;
    private LocalDateTime transactionAt;
    private String type; // charge, use etc..

}

