package io.hhplus.javaconcerthancil.interfaces.api.v1.payment;

import io.hhplus.javaconcerthancil.application.payment.PaymentFacade;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiResponse;
import io.hhplus.javaconcerthancil.interfaces.api.v1.payment.request.PaymentsRequest;
import io.hhplus.javaconcerthancil.interfaces.api.v1.payment.response.PaymentsResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@Tag(   name        =   "05_콘서트_예약",
        description =   "")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
@Log4j2
public class PaymentsController {

    private final PaymentFacade paymentFacade;

    @PostMapping("/users/{userId}")
    public ApiResponse<PaymentsResponse> payments(
            @PathVariable("userId") Long userId,
            @RequestBody PaymentsRequest requestBody
    ){
        log.info("requestBody: {}", requestBody);
        return ApiResponse.success(paymentFacade.completePayment(userId, requestBody));
    }

}
