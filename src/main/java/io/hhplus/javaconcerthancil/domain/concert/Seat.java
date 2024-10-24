package io.hhplus.javaconcerthancil.domain.concert;

import io.hhplus.javaconcerthancil.domain.reservation.ReservationItem;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL)
    private List<ReservationItem> reservationItems = new ArrayList<>(); // 예약 항목 리스트

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

    public void setConcertSchedule(ConcertSchedule concertSchedule) {
        this.concertSchedule = concertSchedule;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }
}
