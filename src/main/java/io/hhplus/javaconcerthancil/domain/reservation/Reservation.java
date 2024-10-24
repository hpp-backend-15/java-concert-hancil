package io.hhplus.javaconcerthancil.domain.reservation;

import io.hhplus.javaconcerthancil.domain.user.User;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 예약을 만든 사용자

    private LocalDateTime reservationAt; // 예약 시간

    @Enumerated(EnumType.STRING)
    private ReservationStatus status; // 예약 상태 (pending, confirmed, cancelled)

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationItem> items = new ArrayList<>(); // 예약 항목 리스트

    // Constructors, Getters, Setters

    // Helper method to add an item to the reservation
    public void addItem(ReservationItem item) {
        items.add(item);
        item.setReservation(this);
    }

    // Helper method to remove an item from the reservation
    public void removeItem(ReservationItem item) {
        items.remove(item);
        item.setReservation(null);
    }


    public Reservation(User user) {
        this.user = user;
        this.reservationAt = LocalDateTime.now();
        this.status = ReservationStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getReservationAt() {
        return reservationAt;
    }

    public List<ReservationItem> getItems() {
        return items;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
