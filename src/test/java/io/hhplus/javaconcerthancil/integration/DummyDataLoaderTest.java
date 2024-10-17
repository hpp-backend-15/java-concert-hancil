package io.hhplus.javaconcerthancil.integration;

import io.hhplus.javaconcerthancil.support.DummyDataLoaderService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DummyDataLoaderTest {
    
    @Autowired
    private DummyDataLoaderService dummyDataLoaderService;

    @BeforeEach
    void setUp() {
        dummyDataLoaderService.loadDummyData();
    }

}
