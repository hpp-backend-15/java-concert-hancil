package io.hhplus.javaconcerthancil.support;

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
