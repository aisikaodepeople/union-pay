package com.unionpay.demo.paysupport;

import cn.hutool.core.map.MapBuilder;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayOrderQueryRequest;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.service.WxPayService;
import com.unionpay.demo.config.WxMchProperties;
import com.unionpay.demo.dto.RefundDTO;
import com.unionpay.demo.util.AssertUtils;
import com.unionpay.demo.util.DateUtils;
import com.unionpay.demo.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * 微信支付公共抽象类
 */
@Slf4j
public abstract class AbstractWxPayService extends AbstractPayService {

    /*通信标识*/
    protected final static String return_code_success = "SUCCESS";
    protected final static String return_code_fail = "FAIL";

    /*业务结果*/
    protected final static String result_code_success = "SUCCESS";
    protected final static String result_code_fail = "FAIL";

    @Resource
    protected WxMchProperties wxMchPayProperties;

    /**
     * 微信支付客户端(抽象接口,具体使用的支付配置,根据实际业务需求自定义实现)
     */
    protected abstract WxPayService getWxPayService() throws Exception;

    /**
     * 微信支付开始时间,时间格式:yyyyMMddHHmmss
     */
    protected String wxPayStartTime(Date currentDate) {
        return DateUtils.format(currentDate, DateUtils.PURE_DATETIME_PATTERN);
    }

    /**
     * 微信支付过期时间,时间格式:yyyyMMddHHmmss
     */
    protected String wxPayExpireTime(Date currentDate) {
        return DateUtils.format(DateUtils.addMinute(currentDate, unionPayConfig.getPayTimeout()), DateUtils.PURE_DATETIME_PATTERN);
    }

    /**
     * 支付金额(单位:分)
     */
    protected int getTotalFee(BigDecimal amount) {
        AssertUtils.notNull(amount, "非法请求");
        return amount.multiply(new BigDecimal(100)).intValue();
    }

    /**
     * 支付金额(单位:分)
     */
    protected int getTotalFee(String amount) {
        AssertUtils.notNull(amount, "非法请求");
        return new BigDecimal(amount).multiply(new BigDecimal(100)).intValue();
    }

    /**
     * 支付金额(单位:元)
     */
    protected BigDecimal getAmount(Integer totalFee) {
        AssertUtils.notNull(totalFee, "非法请求");
        return new BigDecimal(totalFee).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notify(String orderId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String xmlData = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        log.info("微信支付成功回调报文:{}", xmlData);
        AssertUtils.hasText(xmlData, "非法请求");

        try {
            // 验签并解析报文
            WxPayOrderNotifyResult notifyResult = getWxPayService().parseOrderNotifyResult(xmlData);
            if (StringUtils.equals(notifyResult.getReturnCode(), return_code_success) && StringUtils.equals(notifyResult.getResultCode(), result_code_success)) {
                // todo 支付成功业务处理(可根据实际业务调整)
                paySuccessHandle(orderId, notifyResult.getTransactionId(), getAmount(notifyResult.getTotalFee()));
            } else {
                // todo 支付失败业务处理(可根据实际业务调整)
                failHandle(orderId, StringUtils.join(notifyResult.getReturnMsg(), notifyResult.getErrCode(), notifyResult.getErrCodeDes()));
            }
        } catch (Exception e) {
            log.error("微信支付回调异常:", e);

            // todo 支付异常业务处理(可根据实际业务调整)
            failHandle(orderId, e.getMessage());
        }
    }

    @Override
    public ResultVO refund(RefundDTO refundDTO) throws Exception {
        // todo 请求参数校验
        // todo 业务处理

        WxPayRefundResult result = getWxPayService().refund(WxPayRefundRequest.newBuilder()
                .outRefundNo("请求唯一标识xxx")
                .outTradeNo("本地交易流水号xxx")
                .totalFee(getTotalFee(refundDTO.getRefundAmount()))
                .refundFee(getTotalFee(refundDTO.getRefundAmount()))
                .refundDesc(refundDTO.getRefundReason())
                .build());
        return ResultVO.ok(result);
    }

    @Override
    public ResultVO queryBill(String orderId) throws Exception {
        WxPayOrderQueryResult wxPayOrderQueryResult = getWxPayService().queryOrder(WxPayOrderQueryRequest.newBuilder()
                .transactionId("微信交易流水号xxx")
                .build());

        return ResultVO.ok(MapBuilder.create()
                .put("code", wxPayOrderQueryResult.getReturnCode())
                .put("msg", wxPayOrderQueryResult.getReturnMsg())
                .put("orderId", orderId)
                // todo 可根据实际业务选择返回具体的参数
                .put("billData", wxPayOrderQueryResult)
                .build());
    }
}
