# 微信支付宝聚合支付

微信支付开发文档：https://pay.weixin.qq.com/wiki/doc/api/index.html

支付宝支付开发文档：https://opendocs.alipay.com/open/270/105899

*集成 weixin-java-pay、alipay-sdk-java*

```xml

<dependency>
    <groupId>com.alipay.sdk</groupId>
    <artifactId>alipay-sdk-java</artifactId>
    <version>4.35.110.ALL</version>
</dependency>
```

```xml

<dependency>
    <groupId>com.github.binarywang</groupId>
    <artifactId>weixin-java-pay</artifactId>
    <version>4.5.0</version>
</dependency>

```

### 目前支持的支付方式

#### 1. 支付宝电脑网站支付

#### 2. 支付宝当面付

#### 3. 微信电脑网站支付

#### 4. 微信小程序支付

#### curl调用示例

```html
curl --location 'localhost:8080/pay/trade' \
--header 'Content-Type: application/json' \
--data '{
"openId": "",
"orderId": "test001",
"payChannel": "alipaypc",
"returnUrl": ""
}'
```