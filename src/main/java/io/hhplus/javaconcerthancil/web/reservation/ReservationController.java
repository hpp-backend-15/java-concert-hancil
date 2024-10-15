package io.hhplus.javaconcerthancil.web.reservation;

import io.hhplus.javaconcerthancil.domain.ReservationStatus;
import io.hhplus.javaconcerthancil.domain.concert.Seat;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Tag(   name        =   "좌석 예약 요청 API",
        description =   "좌석 예약과 동시에 좌석을 해당 유저에게 임시 배정합니다." +
                        "배정 시간 내 결제가 완료되지 않으면 임시 배정을 해제합니다.")
@RestController
@RequestMapping("/v1/reservation")
@Log4j2
public class ReservationController {

    @PostMapping
    public ReservationResponse reservation(
            HttpServletRequest request,
            @RequestBody ReservationRequest requestBody
    ){
        log.info("token: {}", request.getHeader("Authorization"));
        log.info("requestBody: {}", requestBody);
        List<HashMap<String, Integer>> list = new ArrayList<>();
        HashMap<String, Integer> hm = new HashMap<>();
        HashMap<String, Integer> hm2 = new HashMap<>();
        hm.put("seatNumber", 10);
        hm.put("price", 10_000);
        hm2.put("seatNumber", 11);
        hm2.put("price", 15_000);

        list.add(hm);
        list.add(hm2);

        return new ReservationResponse(1L, 1L, 1L, list, 25000, ReservationStatus.PENDING);
    }
}
