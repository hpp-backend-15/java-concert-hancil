package io.hhplus.javaconcerthancil.support.testcontainer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(TestContainerConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WaitingQueueWithTestContainerTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String ACTIVE_TOKENS_KEY = "active_tokens";
    private static final String WAITING_TOKENS_KEY = "waiting_tokens";

    @BeforeEach
    @DisplayName("Sorted Set 자료구조로 저장되며, " +
            "Key는 토큰을 Score는 요청시간을 Member에는 유저정보를 저장합니다.")
    void setUp() {
        for (int i = 1; i <= 10; i++) {
            redisTemplate.opsForZSet().add(
                    WAITING_TOKENS_KEY,
                    "user:" + i,
                    System.currentTimeMillis()
            );
        }

    }

    @Test
    @DisplayName("key는 토큰, Score는 요청시간, Member에는 유저정보를 저장됩니까?")
    void WaitingTokenTest1() {
        Set<String> members = redisTemplate.opsForZSet().range(WAITING_TOKENS_KEY, 0, -1);
        assert members != null;
        assertThat(members.size()).isEqualTo(10);
        for (String member : members) {
            Double score = redisTemplate.opsForZSet().score(WAITING_TOKENS_KEY, member);
            assert score != null;
            System.out.println("Member: " + member + ", Score: " + milliToLocalDateTime(score));
        }
    }

    @Test
    @DisplayName("user6 앞에 대기인원은 5명있습니다.")
    void WaitingToken_ZRANK_test() {
        String member = "user:6";

        Long rank = redisTemplate.opsForZSet().rank(WAITING_TOKENS_KEY, member);
        assertThat(rank).isNotNull();
        assertThat(rank).isGreaterThan(-1); // rank가 0 이상이어야 함 (멤버가 존재할 경우)
        assertThat(rank).isEqualTo(5);

    }

    private static LocalDateTime milliToLocalDateTime(Double milli) {
        return Instant.ofEpochMilli(milli.longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }


}
