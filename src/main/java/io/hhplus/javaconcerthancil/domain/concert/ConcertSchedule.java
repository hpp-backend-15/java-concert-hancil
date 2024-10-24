package io.hhplus.javaconcerthancil.domain.concert;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
public class ConcertSchedule {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "concert_id")
    private Concert concert;

    private LocalDateTime reservationAvailableAt;
    private LocalDateTime concertAt;

    @OneToMany(mappedBy = "concertSchedule", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Seat> seats;


    public ConcertSchedule(Long id, LocalDateTime availableAt, LocalDateTime concertAt) {
        this.id = id;
        this.reservationAvailableAt = availableAt;
        this.concertAt = concertAt;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getReservationAvailableAt() {
        return reservationAvailableAt;
    }

    public LocalDateTime getConcertAt() {
        return concertAt;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setConcert(Concert concert) {
        this.concert = concert;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}
