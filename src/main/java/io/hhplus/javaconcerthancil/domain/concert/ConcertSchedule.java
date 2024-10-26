package io.hhplus.javaconcerthancil.domain.concert;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private List<Seat> seats = new ArrayList<>();


    public ConcertSchedule(LocalDateTime availableAt, LocalDateTime concertAt) {
        this.reservationAvailableAt = availableAt;
        this.concertAt = concertAt;
    }

    // 좌석 추가 메서드
    public void addSeats() {
        for (int i = 0; i < 50; i++) {
            int seatPrice = (i >= 40) ? 15000 : 10000; // 1~40번 좌석은 10000원, 41~50번 좌석은 15000원
            Seat seat = new Seat(null, i, SeatStatus.AVAILABLE, seatPrice); // id는 null로 설정하여 자동 생성
            seat.setConcertSchedule(this); // 좌석과 콘서트 스케줄 관계 설정
            seats.add(seat); // 좌석 추가
        }
    }



    public void setConcert(Concert concert) {
        this.concert = concert;
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

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}
