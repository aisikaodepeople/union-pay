package com.unionpay.demo.dto;

import com.unionpay.demo.contants.PayChannelEnum;
import lombok.Data;

/**
 * 下单支付参数
 */
@Data
public class TradeDTO {

    /**
     * 微信openId(公众号,小程序支付必传)或者支付宝userId
     */
    private String openId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 支付渠道
     * {@link  PayChannelEnum}
     */
    private String payChannel;

    /**
     * 同步页面地址(前面加上http://或https://)
     */
    private String returnUrl;

}