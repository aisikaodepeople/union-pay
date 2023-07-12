package com.unionpay.demo.exception;

import lombok.Data;

/**
 * 支付自定义异常
 */
@Data
public class UnionPayException extends RuntimeException {

    private String message;

    public UnionPayException(String message) {
        super(message);
        this.message = message;
    }

    public UnionPayException(String message, Throwable throwable) {
        super(message, throwable);
        this.message = message;
    }

    public static UnionPayException unionPayException(String msg, Throwable t, Object... params) {
        return new UnionPayException(String.format(msg, params), t);
    }

    public static UnionPayException unionPayException(String msg, Object... params) {
        return new UnionPayException(String.format(msg, params));
    }

}
