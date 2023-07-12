package com.unionpay.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "alipay.pay")
public class AlipayPayProperties {

    private String gateway;
    private String charset;
    private String signType;
    private String format;
    private String appId;
    private String publicKey;
    private String privateKey;

}
