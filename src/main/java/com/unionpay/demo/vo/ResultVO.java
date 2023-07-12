package com.unionpay.demo.vo;

import lombok.Data;

/**
 * 返回结果封装
 *
 * @param <T>
 */
@Data
public class ResultVO<T> {
    private boolean isSuccess;
    private int code;
    private String msg;
    private T data;

    public static <T> ResultVO<T> ok() {
        return ok(null);
    }

    public static <T> ResultVO<T> ok(T data) {
        return ok(200, "success", data);
    }

    public static <T> ResultVO<T> ok(int code, String msg, T data) {
        ResultVO<T> resultVO = new ResultVO<T>();
        resultVO.setCode(200);
        resultVO.setMsg(msg);
        resultVO.setData(data);
        resultVO.setSuccess(true);
        return resultVO;
    }

    public static <T> ResultVO<T> fail(int code, String msg, T data) {
        ResultVO<T> resultVO = new ResultVO<T>();
        resultVO.setCode(code);
        resultVO.setMsg(msg);
        resultVO.setData(data);
        resultVO.setSuccess(false);
        return resultVO;
    }

    public static <T> ResultVO<T> fail(int code, String msg) {
        return fail(100, msg, null);
    }

    public static <T> ResultVO<T> fail(String msg) {
        return fail(100, msg, null);
    }

}
