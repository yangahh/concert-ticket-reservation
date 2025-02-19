package kr.hhplus.be.server.infrastructure.repository.queuetoken;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class ActiveTokenRedisTemplate {
    private final ZSetOperations<String, Object> zSetOperations;

    public ActiveTokenRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.zSetOperations = redisTemplate.opsForZSet();
    }

    public static String getKey(Long concertId) {
        return "active:concert:" + concertId;
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

    public Long remove(Long concertId, UUID tokenUuid) {
        return zSetOperations.remove(getKey(concertId), tokenUuid);
    }

    public void removeRangeByScore(Long concertId, double min, double max) {
        zSetOperations.removeRangeByScore(getKey(concertId), min, max);
    }

    public Set<ZSetOperations.TypedTuple<Object>> rangeWithScores(Long concertId, long start, long end) {
        return zSetOperations.rangeWithScores(getKey(concertId), start, end);
    }
}
