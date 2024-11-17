package io.hhplus.javaconcerthancil.infrastructure.persistence.reservation;

import io.hhplus.javaconcerthancil.domain.reservation.ReservationItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationItemRepositoryImpl implements ReservationItemRepository {

    private final ReservationItemJpaRepository reservationItemJpaRepository;

    @Override
    public Integer findTotalSeatPriceByReservationId(long reservationId) {
        return reservationItemJpaRepository.findTotalSeatPriceByReservationId(reservationId)
                .orElseThrow();
    }

    @Override
    public List<Long> findSeatIdsByReservationId(Long reservationIdz) {
        return reservationItemJpaRepository.findSeatIdsByReservationId(reservationIdz).orElseThrow();
    }
}
