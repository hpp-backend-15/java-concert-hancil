package io.hhplus.javaconcerthancil.integration.domain.reservation;

import io.hhplus.javaconcerthancil.support.DummyDataLoaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReservationServiceTest {

    @Autowired
    private DummyDataLoaderService dummyDataLoaderService;

    @BeforeEach
    void setUp() {
        dummyDataLoaderService.loadDummyData();
    }


    @Test
    void name() {
    }
}
