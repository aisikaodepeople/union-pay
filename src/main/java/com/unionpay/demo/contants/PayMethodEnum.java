package com.unionpay.demo.contants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * 支付方式
 */
@Getter
@AllArgsConstructor
public enum PayMethodEnum {

    wx("wx", "微信支付"),
    alipay("alipay", "支付宝支付"),
    ;

    private String payMethod;
    private String memo;

    public static PayMethodEnum getPayMethodEnum(String payMethod) {
        return Arrays.stream(PayMethodEnum.values()).filter(t -> StringUtils.equals(t.name(), payMethod)).findFirst().orElse(null);
    }

}
