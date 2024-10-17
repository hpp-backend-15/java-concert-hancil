package io.hhplus.javaconcerthancil.domain.user;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

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

    public User(String name) {
        this.name = name;
        this.balance = 0;
    }
}
