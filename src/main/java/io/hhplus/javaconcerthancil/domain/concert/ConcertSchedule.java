package io.hhplus.javaconcerthancil.domain.concert;

import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.boot.logging.LogLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "concert_schedule")
@NoArgsConstructor
public class ConcertSchedule {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime reservationAvailableAt;

    private LocalDateTime concertAt;

    @ManyToOne
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @OneToMany(mappedBy = "concertSchedule", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Seat> seats = new ArrayList<>();


    public ConcertSchedule(LocalDateTime availableAt, LocalDateTime concertAt) {
        this.reservationAvailableAt = availableAt;
        this.concertAt = concertAt;
        //콘서트 예약가능 및 시작 날짜를 설정함과 동시에 좌석 생성
        addSeats();
    }

    // 좌석 추가 메서드
    private void addSeats() {
        for (int i = 1; i <= 50; i++) {
            int seatPrice = (i >= 41) ? 15000 : 10000; // 1~40번 좌석은 10000원, 41~50번 좌석은 15000원
            Seat seat = new Seat(null, i, SeatStatus.AVAILABLE, seatPrice); // id는 null로 설정하여 자동 생성
            seat.setConcertSchedule(this); // 좌석과 콘서트 스케줄 관계 설정
            seats.add(seat); // 좌석 추가
        }
    }

    public boolean isValid(Long concertId) {
        LocalDateTime now = LocalDateTime.now();

        if (concert == null || !concert.getId().equals(concertId)) {
            throw new ApiException(ErrorCode.E006, LogLevel.INFO);
        }

        if (reservationAvailableAt.isAfter(now) || concertAt.isBefore(now)) {
            throw new ApiException(ErrorCode.E006, LogLevel.INFO, "콘서트 예약이 불가합니다.");
        }
        return true; // 모든 유효성 검사를 통과한 경우
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
