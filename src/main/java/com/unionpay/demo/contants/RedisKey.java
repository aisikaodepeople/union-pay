package com.unionpay.demo.contants;

import java.util.Arrays;

/**
 * redis缓存key
 */
public interface RedisKey {

    /*重复请求加锁*/
    String lockkey_pay = "union-pay:lockkey:pay:";
    String lockkey_paysuccess = "union-pay:lockkey:paysuccess:";

    /*交易流水号*/
    String ORDERNO_REDIS_KEY = "union-pay:orderno:";

    static String formatKey(String key, String... args) {
        return Arrays.stream(args)
                .reduce(new StringBuilder(key),
                        (stringBuilder, arg) -> stringBuilder.append(arg).append(":"),
                        StringBuilder::append)
                .toString()
                .replaceAll(":$", "");
    }

}
