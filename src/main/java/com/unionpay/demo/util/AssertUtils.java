package com.unionpay.demo.util;


import com.unionpay.demo.exception.UnionPayException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * 断言工具类
 */
public class AssertUtils extends Assert {

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new UnionPayException(message);
        }
    }

    public static void hasText(String text, String message) {
        if (StringUtils.isBlank(text)) {
            throw new UnionPayException(message);
        }
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new UnionPayException(message);
        }
    }


}
