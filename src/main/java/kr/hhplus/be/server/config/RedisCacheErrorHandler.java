package kr.hhplus.be.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.lang.Nullable;

// 캐시 장애(역직렬화 실패, 연결 오류 등)가 원본 메서드 실행까지 막지 않도록 예외를 삼키고 로그만 남긴다.
@Slf4j
public class RedisCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Redis cache GET 실패 - cache: {}, key: {}", cache.getName(), key, exception);
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, @Nullable Object value) {
        log.warn("Redis cache PUT 실패 - cache: {}, key: {}", cache.getName(), key, exception);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Redis cache EVICT 실패 - cache: {}, key: {}", cache.getName(), key, exception);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.warn("Redis cache CLEAR 실패 - cache: {}", cache.getName(), exception);
    }
}
