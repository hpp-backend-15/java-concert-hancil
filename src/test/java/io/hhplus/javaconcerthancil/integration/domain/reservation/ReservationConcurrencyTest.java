//package io.hhplus.javaconcerthancil.integration.domain.reservation;
//
//import io.hhplus.javaconcerthancil.domain.concert.Seat;
//import io.hhplus.javaconcerthancil.domain.concert.SeatRepository;
//import io.hhplus.javaconcerthancil.domain.concert.SeatStatus;
//import io.hhplus.javaconcerthancil.domain.reservation.*;
//import io.hhplus.javaconcerthancil.domain.user.User;
//import io.hhplus.javaconcerthancil.domain.user.UserRepository;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.DirtiesContext;
//
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//public class ReservationConcurrencyTest {
//
//    @Autowired
//    private ReservationService reservationService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private SeatRepository seatRepository;
//
//    @Autowired
//    private ReservationRepository reservationRepository;
//
//    @Autowired
//    private ReservationItemRepository reservationItemRepository;
//
//    @Test
//    @DisplayName("1. Thread 방식 - 예약 동시성 테스트")
//    public void testConcurrentReservation() throws InterruptedException {
//
//        Long userAId = 1L;
//        Long userBId = 2L;
//
//        Long concertId = 1L;
//        Long scheduleId = 1L;
//        List<Long> seatIds = List.of(1L, 2L);
//
//        // 사용자 A와 B의 예약 시도
//        Runnable userA = () -> {
//            try {
//                reservationService.reserveConcert(userAId,concertId,scheduleId,seatIds);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        };
//
//        Runnable userB = () -> {
//            try {
//                reservationService.reserveConcert(userBId,concertId,scheduleId,seatIds);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        };
//
//        // 두 사용자 동시에 예약 시도
//        Thread threadA = new Thread(userA);
//        Thread threadB = new Thread(userB);
//        threadA.start();
//        threadB.start();
//        threadA.join();
//        threadB.join();
//
//        // 결과 검증
//        for(Long id : seatIds){
//            Seat updatedSeat = seatRepository.findById(id);
//            assertEquals(SeatStatus.RESERVED, updatedSeat.getStatus());
//        }
//
//    }
//
//    @Test
//    @DisplayName("2. Executor 방식 - 예약 동시성 테스트")
//    @Transactional
//    public void 동시성_좌석예약_비관적락_테스트() throws InterruptedException {
//
//        // 테스트할 좌석 ID 및 사용자 ID
//        List<List<Long>> seatIds = List.of(List.of(1L, 2L, 3L), List.of(2L,3L,10L), List.of(3L,2L,6L), List.of(1L,2L,4L));
//        List<User> users = userRepository.findAll();
//        int totalUsers = users.size();
//        Long concertId = 1L;
//        Long scheduleId = 1L;
//
//        // 동시성 테스트를 위한 스레드 풀
//        ExecutorService executorService = Executors.newFixedThreadPool(totalUsers);
//        CountDownLatch latch = new CountDownLatch(totalUsers);
//
//        // 사용자 1 예약 시도
//            for (User user : users) {
//                executorService.execute(() -> {
//                    try {
//                        reservationService.reserveConcert(user.getId(), concertId, scheduleId, seatIds.get(user.getId().intValue() -1));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        latch.countDown();
//                    }
//                });
//            }
//
//            latch.await(); // 모든 스레드가 작업을 완료할 때까지 대기
//
//        // 예약 데이터는 1개만 생성되어야 함
//        assertThat(reservationRepository.count()).isEqualTo(1);
//
//        //예약한 좌석수에 맞게 저장이 되어야 함
//        assertThat(reservationItemRepository.count()).isEqualTo(seatIds.get(0).size());
//
//        //예약정보가 요청과 일치하는지에 대한 검증
//        List<Reservation> all = reservationRepository.findAll();
//        for(Reservation reservation : all){
//            List<ReservationItem> items = reservation.getItems();
//            for(ReservationItem item : items){
//                List<Long> longs = seatIds.get(reservation.getUser().getId().intValue() - 1);
//                assertTrue(longs.contains(item.getSeat().getId()));
//            }
//        }
//    }
//
//}
//
//
