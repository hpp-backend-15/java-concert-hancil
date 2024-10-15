package io.hhplus.javaconcerthancil.domain.concert;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private ConcertSchedule concertSchedule;

    private int seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    private int seatPrice;

    public Seat(Long id, int seatNumber, SeatStatus status, int seatPrice) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.status = status;
        this.seatPrice = seatPrice;
    }

    public Long getId() {
        return id;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public int getSeatPrice() {
        return seatPrice;
    }
}
