package com.unionpay.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * redis锁工具类
 */
@Slf4j
public class RedisLockUtil {
    private static RedissonClient redissonClient;

    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 加锁
     *
     * @param lockKey
     * @return
     */
    public static RLock lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    /**
     * 释放锁
     * <p>多线程环境下会失效报错<p/>
     *
     * @param lockKey
     */
    public static void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
    }

    /**
     * 带超时的锁
     *
     * @param lockKey
     * @param timeout 毫秒
     * @return
     */
    public static RLock lock(String lockKey, int timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, TimeUnit.MILLISECONDS);
        return lock;
    }

    /**
     * 尝试加锁
     *
     * @param lockKey
     * @param waitTime  等待时间 毫秒
     * @param leaseTime 锁释放时间 毫秒
     * @return
     */
    public static boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.error("尝试加锁失败:", e);
            return false;
        }
    }

    /**
     * 默认加锁 等待3秒，10秒失效
     *
     * @param lockKey
     * @return
     */
    public static boolean defaultTryLock(String lockKey) {
        return tryLock(lockKey, 3000, 10000);
    }

}