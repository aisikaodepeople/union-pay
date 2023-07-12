package com.unionpay.demo.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Data
@Configuration
@EnableConfigurationProperties({
        AlipayPayProperties.class,
        WxMchProperties.class,
        WxMiniPayProperties.class,
        WxPcPayProperties.class,
        // 继续添加其他支付配置...
})
public class UnionPayConfig {

    @Value("${callback}")
    private String callback;
    @Value("${payTimeout}")
    private int payTimeout;

    @Resource
    private AlipayPayProperties alipayPayProperties;

    /**
     * 支付宝客户端
     */
    @Bean
    public AlipayClient getAlipayClient() {
        return new DefaultAlipayClient(
                alipayPayProperties.getGateway(),
                alipayPayProperties.getAppId(),
                alipayPayProperties.getPrivateKey(),
                alipayPayProperties.getFormat(),
                alipayPayProperties.getCharset(),
                alipayPayProperties.getPublicKey(),
                alipayPayProperties.getSignType()
        );
    }

}
