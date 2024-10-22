package io.hhplus.javaconcerthancil.interfaces.api.v1.reservation;

import io.hhplus.javaconcerthancil.application.reservation.ReservationFacade;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiResponse;
import io.hhplus.javaconcerthancil.interfaces.api.v1.reservation.request.ReservationRequest;
import io.hhplus.javaconcerthancil.interfaces.api.v1.reservation.response.ReservationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(   name        =   "03_좌석 예약 요청 API",
        description =   "좌석 예약과 동시에 좌석을 해당 유저에게 임시 배정합니다." +
                        "배정 시간 내 결제가 완료되지 않으면 임시 배정을 해제합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reservation")
@Log4j2
public class ReservationController {

    private final ReservationFacade reservationFacade;

    @PostMapping
    public ApiResponse<ReservationResponse> reservation(
            HttpServletRequest request,
            @RequestBody ReservationRequest requestBody
    ){
        log.info("token: {}", request.getHeader("Authorization"));
        log.info("requestBody: {}", requestBody);




//        List<HashMap<String, Integer>> list = new ArrayList<>();
//        HashMap<String, Integer> hm = new HashMap<>();
//        HashMap<String, Integer> hm2 = new HashMap<>();
//        hm.put("seatNumber", 10);
//        hm.put("price", 10_000);
//        hm2.put("seatNumber", 11);
//        hm2.put("price", 15_000);
//
//        list.add(hm);
//        list.add(hm2);

        return ApiResponse.success(reservationFacade.reserveConcert(request, requestBody));
    }
}
