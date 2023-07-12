package com.unionpay.demo.contants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;

/**
 * 支付渠道
 */
@Getter
@AllArgsConstructor
public enum PayChannelEnum {

    wxpc("wxpc", "wx", "NATIVE", "微信电脑网站支付"),
    wxmini("wxmini", "wx", "JSAPI", "微信小程序支付"),

    alipaypc("alipaypc", "alipay", "alipay.trade.page.pay", "支付宝电脑网站支付"),
    alipayf2f("alipayf2f", "alipay", "alipay.trade.precreate", "支付宝当面付-扫码支付"),
    ;

    // 支付渠道
    private String payChannel;
    // 支付方式
    private String payMethod;
    // 支付平台API
    private String payApi;
    // 描述
    private String memo;

    /**
     * 是否为微信支付
     *
     * @param payChannel
     * @return
     */
    public static boolean isWx(String payChannel) {
        PayChannelEnum payChannelEnum = PayChannelEnum.valueOf(payChannel);
        return payChannelEnum != null && StringUtils.equals(payChannelEnum.getPayMethod(), "wx");
    }

    /**
     * 是否为支付宝支付
     *
     * @param payChannel
     * @return
     */
    public static boolean isAlipay(String payChannel) {
        PayChannelEnum payChannelEnum = PayChannelEnum.valueOf(payChannel);
        return payChannelEnum != null && StringUtils.equals(payChannelEnum.getPayMethod(), "alipay");
    }

    /**
     * 获取支付渠道
     *
     * @param payChannel
     * @return
     */
    public static PayChannelEnum getPayChannelEnum(String payChannel) {
        return EnumSet.allOf(PayChannelEnum.class).stream().filter(t -> StringUtils.equals(t.getPayChannel(), payChannel)).findFirst().orElse(null);
    }

}
