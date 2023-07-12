package com.unionpay.demo.util;

import com.unionpay.demo.contants.RedisKey;
import com.unionpay.demo.exception.UnionPayException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 交易订单号工具类
 */
@Component
public final class OrderNoUtils {

    private static RedisTemplate redisTemplate;
    private final static int TTL = 2 * 24 * 60 * 60;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        OrderNoUtils.redisTemplate = redisTemplate;
    }

    /**
     * 创建交易订单号
     */
    public static String generateOrderNo() throws UnionPayException {
        String currentDay = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String redisKey = RedisKey.formatKey(RedisKey.ORDERNO_REDIS_KEY, currentDay);

        try {
            return StringUtils.join(currentDay, redisTemplate.boundValueOps(redisKey).increment(1));
        } catch (Exception e) {
            throw UnionPayException.unionPayException("创建交易订单号失败: %s", e.getCause());
        } finally {
            redisTemplate.expire(redisKey, TTL, TimeUnit.SECONDS);
        }
    }

}
