package io.hhplus.javaconcerthancil.domain.reservation;

import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReservationItemRepository {

    Integer findTotalSeatPriceByReservationId(long reservationId);
    List<Long> findSeatIdsByReservationId(Long reservationIdz);
}
