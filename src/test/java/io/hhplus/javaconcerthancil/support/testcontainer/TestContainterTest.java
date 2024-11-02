package io.hhplus.javaconcerthancil.support.testcontainer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(TestContainerConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestContainterTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String ACTIVE_TOKENS_KEY = "active_tokens";
    private static final String WAITING_TOKENS_KEY = "waiting_tokens";


    @Test
    public void testAddAndRemoveActiveToken() {
        // 1. Add an active token
        String token = "token:12345";
        redisTemplate.opsForSet().add(ACTIVE_TOKENS_KEY, token);

        // 2. Verify the token was added
        Set<String> activeTokens = redisTemplate.opsForSet().members(ACTIVE_TOKENS_KEY);
        assertThat(activeTokens).contains(token);

        // 3. Remove the token
        redisTemplate.opsForSet().remove(ACTIVE_TOKENS_KEY, token);

        // 4. Verify the token was removed
        activeTokens = redisTemplate.opsForSet().members(ACTIVE_TOKENS_KEY);
        assertThat(activeTokens).doesNotContain(token);
    }
}
