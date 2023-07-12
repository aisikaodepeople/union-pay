package com.unionpay.demo.config;


import com.unionpay.demo.util.RedisLockUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedissonConfig {

    @Value("${spring.redis.database}")
    private int database;
    @Value("redis://${spring.redis.host}:${spring.redis.port}")
    private String address;
    @Value("${spring.redis.timeout}")
    private int timeout;

    @Bean
    public RedisTemplate<String, Serializable> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(genericJackson2JsonRedisSerializer);

        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        template.setConnectionFactory(lettuceConnectionFactory);
        return template;
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setDatabase(database)
                .setAddress(address)
                .setTimeout(timeout)
                .setPingConnectionInterval(1000);
        return Redisson.create(config);
    }

    @Bean
    public RedisLockUtil redisLockUtil(RedissonClient redissonClient) {
        RedisLockUtil redisLockUtil = new RedisLockUtil();
        redisLockUtil.setRedissonClient(redissonClient);
        return redisLockUtil;
    }
}
