package com.unionpay.demo.paysupport;

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.unionpay.demo.config.WxMiniPayProperties;
import com.unionpay.demo.contants.PayChannelEnum;
import com.unionpay.demo.dto.TradeDTO;
import com.unionpay.demo.util.AssertUtils;
import com.unionpay.demo.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;


@Slf4j
@Component("wxmini")
public class WxminiPayService extends AbstractWxPayService {

    @Resource
    protected WxMiniPayProperties wxMiniPayProperties;

    @Override
    protected WxPayService getWxPayService() {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(wxMiniPayProperties.getAppId());
        payConfig.setMchId(wxMchPayProperties.getMchId());
        payConfig.setMchKey(wxMchPayProperties.getMchKey());
        // todo 微信证书,退款时需要
        payConfig.setKeyPath("classpath:apiclient_cert.p12");
        payConfig.setUseSandboxEnv(false);

        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }

    @Override
    public ResultVO trade(TradeDTO tradeDTO) throws Exception {
        AssertUtils.hasText(tradeDTO.getOpenId(), "微信小程序支付需要传入openId");
        Date now = currentDate();
        String orderId = tradeDTO.getOrderId();

        // todo 下单前业务处理(可根据实际业务调整)
        tradePreHandle(tradeDTO, now);

        try {
            WxPayMpOrderResult result = getWxPayService().createOrder(WxPayUnifiedOrderRequest.newBuilder()
                    .openid(tradeDTO.getOpenId())
                    .spbillCreateIp("127.0.0.1")
                    .timeStart(wxPayStartTime(now))
                    .timeExpire(wxPayExpireTime(now))
                    .tradeType(PayChannelEnum.wxmini.getPayApi())
                    .productId("商品Id")
                    .totalFee(getTotalFee("订单金额xxx分"))
                    .body("商品描述xxx")
                    .outTradeNo("本地交易流水号xxx")
                    // todo 支付成功回调地址(可根据实际业务调整)
                    .notifyUrl(getNotifyUrl(tradeDTO.getPayChannel(), orderId))
                    .build());

            // todo 下单后成功业务处理(可根据实际业务调整)
            tradeSuccessHandle(orderId);
            return ResultVO.ok(result);
        } catch (WxPayException e) {
            log.error("微信小程序支付错误:", e);

            // todo 失败业务处理(可根据实际业务调整)
            failHandle(orderId, e.getReturnMsg());
            return ResultVO.fail("下单失败");
        } catch (Exception e) {
            log.error("微信小程序支付错误:", e);

            // todo 失败业务处理,,参数可根据实际业务调整
            failHandle(orderId, e.getMessage());
            return ResultVO.fail("下单失败");
        }
    }

}
