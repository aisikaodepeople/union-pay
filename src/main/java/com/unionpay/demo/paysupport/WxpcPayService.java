package com.unionpay.demo.paysupport;

import com.github.binarywang.wxpay.bean.order.WxPayNativeOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.unionpay.demo.config.WxPcPayProperties;
import com.unionpay.demo.contants.PayChannelEnum;
import com.unionpay.demo.dto.TradeDTO;
import com.unionpay.demo.util.AssertUtils;
import com.unionpay.demo.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Component("wxpc")
public class WxpcPayService extends AbstractWxPayService {

    @Resource
    protected WxPcPayProperties wxPcPayProperties;

    @Override
    protected WxPayService getWxPayService() {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(wxPcPayProperties.getAppId());
        payConfig.setMchId(wxMchPayProperties.getMchId());
        payConfig.setMchKey(wxMchPayProperties.getMchKey());
        // todo 微信证书,退款时需要
        payConfig.setKeyPath("classpath:wxcert.p12");
        payConfig.setUseSandboxEnv(false);

        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO trade(TradeDTO tradeDTO) throws Exception {
        Date now = currentDate();
        String orderId = tradeDTO.getOrderId();

        // todo 下单前业务处理(可根据实际业务调整)
        tradePreHandle(tradeDTO, now);

        try {
            WxPayNativeOrderResult result = getWxPayService().createOrder(WxPayUnifiedOrderRequest.newBuilder()
                    .tradeType(PayChannelEnum.wxpc.getPayApi())
                    .spbillCreateIp("127.0.0.1")
                    // todo 支付成功回调地址(可根据实际业务调整)
                    .notifyUrl(getNotifyUrl(tradeDTO.getPayChannel(), orderId))
                    .totalFee(getTotalFee("订单金额xxx分"))
                    .productId("商品ID")
                    .body("商品描述")
                    .outTradeNo("本地交易流水号xxx")
                    .build());

            AssertUtils.hasText(result.getCodeUrl(), "未获取到codeUrl");

            // todo 下单后成功业务处理(可根据实际业务调整)
            tradeSuccessHandle(tradeDTO.getOrderId());
            return ResultVO.ok(result.getCodeUrl());
        } catch (WxPayException e) {
            log.error("微信电脑网站支付错误:", e);

            // todo 失败业务处理(可根据实际业务调整)
            failHandle(orderId, e.getReturnMsg());
            return ResultVO.fail("下单失败");
        } catch (Exception e) {
            log.error("微信电脑网站支付错误:", e);

            // todo 失败业务处理(可根据实际业务调整)
            failHandle(orderId, e.getMessage());
            return ResultVO.fail("下单失败");
        }
    }

}
