package io.hhplus.javaconcerthancil.application.reservation;

import io.hhplus.javaconcerthancil.domain.ReservationStatus;
import io.hhplus.javaconcerthancil.domain.concert.ConcertService;
import io.hhplus.javaconcerthancil.domain.reservation.Reservation;
import io.hhplus.javaconcerthancil.domain.reservation.ReservationItem;
import io.hhplus.javaconcerthancil.domain.reservation.ReservationService;
import io.hhplus.javaconcerthancil.domain.waitingqueue.WaitingQueueService;
import io.hhplus.javaconcerthancil.interfaces.api.v1.reservation.request.ReservationRequest;
import io.hhplus.javaconcerthancil.interfaces.api.v1.reservation.response.ReservationResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationFacade {

    private final WaitingQueueService queueService;
    private final ReservationService reservationService;
    private final ConcertService concertService;


    public ReservationResponse reserveConcert(ReservationRequest requestBody) {

        //3. 예약저장
        Reservation reservation = reservationService.reserveConcert(
                requestBody.userId(), requestBody.concertId(),
                requestBody.scheduleId(), requestBody.seatIds());

        //총합 구하기
        long totalPrice = reservation.getItems().stream()
                .mapToLong(ReservationItem::getSeatPrice)
                .sum();

        return new ReservationResponse(
                reservation.getId()
                , requestBody.concertId()
                , requestBody.scheduleId()
                , requestBody.seatIds()
                , totalPrice
                , ReservationStatus.PENDING);
    }
}
