package io.hhplus.javaconcerthancil.infrastructure.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.javaconcerthancil.domain.concert.Concert;
import io.hhplus.javaconcerthancil.domain.concert.ConcertSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class ConcertScheduleRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

//    public void saveConcertSchedules(Long concertId, List<ConcertSchedule> concertSchedules) {
//        String key = "concert" + ":" + concertId + ":" + "concertSchedules";
//        redisTemplate.opsForValue().set(key, concertSchedules);
//        redisTemplate.expire(key, 24, TimeUnit.HOURS);
//    }

    public void saveConcert(long concertId, Concert foundConcert) {
        String key = "concert" + ":" + concertId;
        redisTemplate.opsForValue().set(key, foundConcert);
        redisTemplate.expire(key, 24, TimeUnit.SECONDS);
    }

    public Concert getConcert(long concertId) {
        Object concertData = redisTemplate.opsForValue().get("concert:" + concertId);
        return objectMapper.convertValue(concertData, Concert.class);
    }

    public List<ConcertSchedule> getConcertSchedules(Long concertId) {
        String key = "concert" + ":" + concertId + ":" + "concertSchedules";
        return (List<ConcertSchedule>) redisTemplate.opsForValue().get(key);
    }

//    public void deleteAllConcertSchedules(Long concertId) {
//        String pattern = "concert:" + concertId + ":concertSchedules";
//        Set<String> keys = redisTemplate.keys(pattern);
//        if (keys != null && !keys.isEmpty()) {
//            redisTemplate.delete(keys);
//        }
//    }

//    public void deleteConcertSchedules(Long concertId) {
//        String key = "concert" + ":" + concertId + ":" + "concertSchedules";
//        redisTemplate.delete(key);
//    }

}
