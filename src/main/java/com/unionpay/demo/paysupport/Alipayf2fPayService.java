package com.unionpay.demo.paysupport;

import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.unionpay.demo.dto.TradeDTO;
import com.unionpay.demo.util.AssertUtils;
import com.unionpay.demo.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Slf4j
@Component("alipayf2f")
public class Alipayf2fPayService extends AbstractAlipayService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVO trade(TradeDTO tradeDTO) throws Exception {
        Date now = currentDate();
        String orderId = tradeDTO.getOrderId();

        // todo 下单前处理(可根据实际业务调整)
        tradePreHandle(tradeDTO, now);

        try {
            AlipayTradePrecreateModel precreateModel = new AlipayTradePrecreateModel();
            precreateModel.setOutTradeNo("本地交易流水号xxx");
            precreateModel.setTotalAmount("交易金额xxx");
            precreateModel.setSubject("订单标题xxx");
            precreateModel.setBody("订单附加信息xxx");
            precreateModel.setProductCode("FACE_TO_FACE_PAYMENT");
            precreateModel.setTimeExpire(alipayExpireTime(now));

            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
            request.setBizModel(precreateModel);
            // todo 支付成功回调地址(可根据实际业务调整)
            request.setNotifyUrl(getNotifyUrl(tradeDTO.getPayChannel(), orderId));

            // 调用支付宝当面付接口
            AlipayTradePrecreateResponse payResponse = alipayClient.execute(request);

            // 下单是否成功
            AssertUtils.isTrue(payResponse.isSuccess(), payResponse.getSubMsg());

            // todo 下单成功业务处理(可根据实际业务调整)
            tradeSuccessHandle(tradeDTO.getOrderId());

            return ResultVO.ok(payResponse.getQrCode());
        } catch (Exception e) {
            log.error("支付宝当面付扫码支付错误:", e);

            // todo 失败业务处理(可根据实际业务调整)
            failHandle(orderId, e.getMessage());
            return ResultVO.fail("下单失败");
        }
    }

}
