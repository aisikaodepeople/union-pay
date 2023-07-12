package com.unionpay.demo.exception;

import com.unionpay.demo.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理(根据实际业务,可自行添加其他异常处理)
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResultVO handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ResultVO.fail(e.getMessage());
    }

}