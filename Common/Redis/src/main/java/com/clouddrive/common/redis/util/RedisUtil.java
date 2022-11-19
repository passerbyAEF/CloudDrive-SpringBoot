package com.clouddrive.common.redis.util;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisUtil {

    RedisTemplate<String, Object> redisTemplate;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addStringAndSetTimeOut(String key, String value, int time) {
        addStringAndSetTimeOut(key, value, time, TimeUnit.MINUTES);
    }

    public void addStringAndSetTimeOut(String key, String value, int time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, time, timeUnit);
    }

    public void removeString(String key) {
        redisTemplate.opsForValue().getOperations().delete(key);
    }

    public void addString(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void addSet(String setName, String... value) {
        redisTemplate.opsForSet().add(setName, value);
    }

    public void findSet(String setName, String value) {
        redisTemplate.opsForSet().isMember(setName, value);
    }

    public void removeSet(String key) {
        redisTemplate.delete(key);
    }

    public String getString(String key) {
        try {
            return redisTemplate.opsForValue().get(key).toString();
        } catch (Exception e) {
            return null;
        }
    }
}
