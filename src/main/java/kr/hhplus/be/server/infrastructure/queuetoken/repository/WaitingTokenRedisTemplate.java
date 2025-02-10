package kr.hhplus.be.server.infrastructure.queuetoken.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class WaitingTokenRedisTemplate {
    private final ZSetOperations<String, Object> zSetOperations;

    public WaitingTokenRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.zSetOperations = redisTemplate.opsForZSet();
    }

    public static String getKey(Long concertId) {
        return "waiting:concert:" + concertId;
    }

    public Boolean add(Long concertId, UUID tokenUuid, Double score) {
        return zSetOperations.add(getKey(concertId), tokenUuid, score);
    }

    public Double getScore(Long concertId, UUID tokenUuid) {
        return zSetOperations.score(getKey(concertId), tokenUuid);
    }

    public Long size(Long concertId) {
        return zSetOperations.size(getKey(concertId));
    }

    public Long getRank(Long concertId, UUID tokenUuid) {
        return zSetOperations.rank(getKey(concertId), tokenUuid);
    }

    public Set<TypedTuple<Object>> popMin(Long concertId, int count) {
        return zSetOperations.popMin(getKey(concertId), count);
    }

    public void removeRangeByScore(Long concertId, double min, double max) {
        zSetOperations.removeRangeByScore(getKey(concertId), min, max);
    }

    public Set<Object> range(Long concertId, long start, long end) {
        return zSetOperations.range(getKey(concertId), start, end);
    }

    public Set<TypedTuple<Object>> rangeWithScores(Long concertId, long start, long end) {
        return zSetOperations.rangeWithScores(getKey(concertId), start, end);
    }
}
