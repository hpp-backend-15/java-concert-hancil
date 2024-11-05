package io.hhplus.javaconcerthancil.support.testcontainer;

import io.hhplus.javaconcerthancil.domain.waitingqueue.QueueStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(TestContainerConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WaitingQueueWithTestContainerTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

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


    @Test
    @DisplayName("Redis에 유저 ID별 큐의 활성화상태를 저장하고 값을 정상적으로 조회합니다")
    void ActiveToken_test1() {

        for (long i = 1; i <= 10; i++) {
            redisTemplate.opsForValue().set("userId:"+i, QueueStatus.PROGRESS.toString());
        }

        for (long i = 1; i <= 10; i++) {
            String s = redisTemplate.opsForValue().get("userId:6");
            assert s != null;
            assertThat(s).isEqualTo(QueueStatus.PROGRESS.toString());
        }

    }


    @Test
    @DisplayName("Redis에 저장된 데이터의 만료 시간 설정 및 만료 후 삭제를 확인합니다.")
    public void testExpiration() throws InterruptedException {
        // Given
        String key = "userId:1";
        String value = QueueStatus.PROGRESS.toString();

        // When
        // 데이터를 Redis에 저장하고, 1초의 만료 시간을 설정
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        valueOps.set(key, value, Duration.ofSeconds(1));

        // 저장된 데이터가 Redis에 존재하는지 확인
        assertThat(valueOps.get(key)).isEqualTo(value);

        // 만료 시간을 기다림
        TimeUnit.SECONDS.sleep(2);

        // Then
        // 데이터가 만료되어 삭제되었는지 확인
        assertThat(redisTemplate.opsForValue().get(key)).isNull();
    }



    private static LocalDateTime milliToLocalDateTime(Double milli) {
        return Instant.ofEpochMilli(milli.longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }


}
