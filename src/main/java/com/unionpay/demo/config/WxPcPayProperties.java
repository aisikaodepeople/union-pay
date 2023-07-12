package com.unionpay.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信电脑网站支付配置
 */
@Data
@ConfigurationProperties(prefix = "wxpc.pay")
public class WxPcPayProperties {
    private String appId;
    private String secret;
}
