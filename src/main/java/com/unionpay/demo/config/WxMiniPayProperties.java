package com.unionpay.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信小程序支付配置
 */
@Data
@ConfigurationProperties(prefix = "wxmini.pay")
public class WxMiniPayProperties {
    private String appId;
    private String secret;
}
