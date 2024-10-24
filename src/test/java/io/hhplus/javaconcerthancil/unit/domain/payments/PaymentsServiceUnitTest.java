package io.hhplus.javaconcerthancil.unit.domain.payments;

import io.hhplus.javaconcerthancil.domain.payments.PaymentRepository;
import io.hhplus.javaconcerthancil.domain.payments.PaymentsService;
import io.hhplus.javaconcerthancil.domain.reservation.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PaymentsServiceUnitTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private PaymentsService paymentsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void name() {

        //given
        Long userId = 1L;
        Long reservationId = 1L;


        paymentRepository.findByReservationId(reservationId);

    }
}
