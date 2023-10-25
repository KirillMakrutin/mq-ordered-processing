package com.example.consumer.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
public class LockingService {
    private final RedissonClient redissonClient;

    public LockingService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public RLock getLock(String resourceName) {
        return redissonClient.getLock(resourceName);
    }
}
