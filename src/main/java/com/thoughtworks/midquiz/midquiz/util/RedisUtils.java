package com.thoughtworks.midquiz.midquiz.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisUtils<T> {
    private final RedisTemplate redisTemplate;

    public RedisUtils(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveList(String key, List<T> value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    public List<T> getList(String key) {
        return (List<T>) redisTemplate.opsForList().rightPop(key);
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }


}
