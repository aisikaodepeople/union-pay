package com.unionpay.demo.dto;

import com.unionpay.demo.contants.PayChannelEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 退款参数
 */
@Data
public class RefundDTO {

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 支付渠道
     * {@link PayChannelEnum}
     */
    private String payChannel;

    /**
     * 退款金额,单位:元
     */
    private BigDecimal refundAmount;

    /**
     * 退款的原因说明
     */
    private String refundReason;

}
