package com.unionpay.demo.paysupport;

import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.unionpay.demo.dto.TradeDTO;
import com.unionpay.demo.util.AssertUtils;
import com.unionpay.demo.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Component("alipaypc")
public class AlipaypcPayService extends AbstractAlipayService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO trade(TradeDTO tradeDTO) throws Exception {
        Date now = currentDate();
        String orderId = tradeDTO.getOrderId();

        // todo 下单前业务处理(可根据实际业务调整)
        tradePreHandle(tradeDTO, now);

        try {
            AlipayTradePagePayModel pagePayModel = new AlipayTradePagePayModel();
            pagePayModel.setOutTradeNo("本地交易流水号xxx");
            pagePayModel.setTotalAmount("交易金额xxx");
            pagePayModel.setSubject("订单标题xxx");
            pagePayModel.setBody("订单附加信息xxx");
            pagePayModel.setProductCode("FAST_INSTANT_TRADE_PAY");
            pagePayModel.setTimeExpire(alipayExpireTime(now));

            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            alipayRequest.setBizModel(pagePayModel);
            alipayRequest.setReturnUrl(tradeDTO.getReturnUrl());
            // todo 支付成功回调地址(可根据实际业务调整)
            alipayRequest.setNotifyUrl(getNotifyUrl(tradeDTO.getPayChannel(), orderId));

            // 请求支付宝电脑网站支付接口
            AlipayTradePagePayResponse payResponse = alipayClient.pageExecute(alipayRequest);
            // 判断下单是否成功
            AssertUtils.isTrue(payResponse.isSuccess(), payResponse.getSubMsg());

            // todo 下单后成功业务处理(可根据实际业务调整)
            tradeSuccessHandle(tradeDTO.getOrderId());

            return ResultVO.ok(payResponse.getBody());
        } catch (Exception e) {
            log.error("支付宝电脑网站支付错误:", e);

            // todo 失败业务处理(可根据实际业务调整)
            failHandle(orderId, e.getMessage());

            return ResultVO.fail("下单失败");
        }
    }

}
