package io.hhplus.javaconcerthancil.domain.reservation;

import io.hhplus.javaconcerthancil.domain.concert.Seat;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class ReservationItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation; // 해당 예약과의 연관

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat; // 예약된 좌석

    private long seatPrice; // 좌석 가격

    // Constructors, Getters, Setters


    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public void setSeatPrice(long seatPrice) {
        this.seatPrice = seatPrice;
    }

    public long getSeatPrice() {
        return seatPrice;
    }


}