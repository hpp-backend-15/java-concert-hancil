package io.hhplus.javaconcerthancil.application.concert;


import io.hhplus.javaconcerthancil.domain.concert.Concert;
import io.hhplus.javaconcerthancil.domain.concert.ConcertService;
import io.hhplus.javaconcerthancil.domain.concert.Seat;
import io.hhplus.javaconcerthancil.domain.waitingqueue.WaitingQueueService;
import io.hhplus.javaconcerthancil.interfaces.api.v1.concert.response.GetConcertSchedulesResponse;
import io.hhplus.javaconcerthancil.interfaces.api.v1.concert.response.GetConcertSeatsRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ConcertFacade {

    private final WaitingQueueService queueService;
    private final ConcertService concertService;

    public GetConcertSchedulesResponse getConcertSchedules(HttpServletRequest request, Long concertId) {


        /**
         * todo: 토큰 만료 검증은 filter 혹은 interceptor에서 처리할 것
         */

        //1. 토큰 만료 검증
        String token = request.getHeader("Authorization");
        boolean isActive = queueService.isActiveToken(token);

        //2. 토큰이 없거나 비활성화된 상태면 에러
        if (!isActive){
            throw new IllegalArgumentException("invalid token");
        }

        //3. 예약 가능한 콘서트 조회
        Concert scheduledConcert = concertService.getScheduledConcert(concertId);

        return new GetConcertSchedulesResponse(1L, scheduledConcert.getSchedules());
    }

    public GetConcertSeatsRequest getConcertSeats(HttpServletRequest request, Long concertId, Long scheduleId) {

        //1. 토큰 만료 검증
        String token = request.getHeader("Authorization");
        boolean isActive = queueService.isActiveToken(token);

        //2. 토큰이 없거나 비활성화된 상태면 에러
        if (!isActive){
            throw new IllegalArgumentException("invalid token");
        }

        List<Seat> concertSeats = concertService.getConcertSeats(concertId, scheduleId);

        return new GetConcertSeatsRequest(concertId, scheduleId, concertSeats);
    }
}
