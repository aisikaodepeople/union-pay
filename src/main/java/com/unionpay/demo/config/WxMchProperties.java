package com.unionpay.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信商户号配置
 */
@Data
@ConfigurationProperties("wxmch.pay")
public class WxMchProperties {
    private String mchName;
    private String mchId;
    private String mchKey;
}
