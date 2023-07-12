package com.unionpay.demo.paysupport;

import cn.hutool.core.map.MapBuilder;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.unionpay.demo.config.AlipayPayProperties;
import com.unionpay.demo.dto.RefundDTO;
import com.unionpay.demo.util.AssertUtils;
import com.unionpay.demo.util.DateUtils;
import com.unionpay.demo.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支付宝支付公共抽象类
 */
@Slf4j
public abstract class AbstractAlipayService extends AbstractPayService {

    @Resource
    protected AlipayPayProperties alipayPayProperties;

    @Resource
    protected AlipayClient alipayClient;

    /**
     * 支付宝支付过期时间(根据业务需求可自定义超时时间)
     *
     * @param currentDate
     * @return
     */
    protected String alipayExpireTime(Date currentDate) {
        return DateUtils.format(DateUtils.addMinute(currentDate, unionPayConfig.getPayTimeout()), DateUtils.NORM_DATETIME_PATTERN);
    }

    /**
     * 支付金额(单位:元,两位小数)
     *
     * @param totalAmount
     * @return
     */
    protected BigDecimal getAmount(String totalAmount) {
        return new BigDecimal(totalAmount);
    }

    /**
     * 支付金额(单位:元,两位小数)
     *
     * @param totalAmount
     * @return
     */
    protected String getAmount(BigDecimal totalAmount) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(totalAmount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notify(String payRecordId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            Map<String, String[]> requestParams = request.getParameterMap();

            AssertUtils.notNull(requestParams, "非法请求");
            Map<String, String> params = requestParams.entrySet().stream()
                    .filter(entry -> entry != null && entry.getKey() != null && entry.getValue() != null)
                    .collect(Collectors.toMap(e -> e.getKey(), e -> String.join(",", e.getValue())));
            log.info("支付宝支付状态:tradeStatus={}", request.getParameter("trade_status"));

            // 调用SDK验证签名
            boolean signSuccess = AlipaySignature.rsaCheckV1(params, alipayPayProperties.getPublicKey(), alipayPayProperties.getCharset(), alipayPayProperties.getSignType());
            AssertUtils.isTrue(signSuccess, "验签失败");
            paySuccessHandle(payRecordId, request.getParameter("trade_no"), getAmount(request.getParameter("total_amount")));
        } catch (Exception e) {
            log.error("支付宝回调异常:", e);
            failHandle(payRecordId, e.getMessage());
        }
    }

    @Override
    public ResultVO refund(RefundDTO refundDTO) throws Exception {
        String orderId = refundDTO.getOrderId();

        // todo 校验请求是否合法
        // todo 业务处理

        AlipayTradeRefundModel refundModel = new AlipayTradeRefundModel();
        refundModel.setOutRequestNo("请求的唯一标识xxx");
        refundModel.setOutTradeNo("本地交易流水号xxx");
        refundModel.setTradeNo("支付宝交易流水号xxx");
        refundModel.setRefundAmount(getAmount(refundDTO.getRefundAmount()));
        refundModel.setRefundReason(refundDTO.getRefundReason());

        AlipayTradeRefundRequest refundRequest = new AlipayTradeRefundRequest();
        refundRequest.setBizModel(refundModel);

        AlipayTradeRefundResponse refundResponse = alipayClient.execute(refundRequest);
        if (refundResponse.isSuccess()) {
            return ResultVO.ok();
        }
        return ResultVO.fail(refundResponse.getSubMsg());
    }

    @Override
    public ResultVO queryBill(String orderId) throws Exception {
        AlipayTradeQueryModel queryModel = new AlipayTradeQueryModel();
        queryModel.setOutTradeNo("本地交易流水号xxx");
        queryModel.setTradeNo("支付宝交易流水号xxx");

        AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
        queryRequest.setBizModel(queryModel);

        AlipayTradeQueryResponse tradeQueryResponse = alipayClient.execute(queryRequest);

        return ResultVO.ok(MapBuilder.create()
                .put("code", tradeQueryResponse.getCode())
                .put("msg", tradeQueryResponse.getMsg())
                .put("orderId", orderId)
                // todo 可根据实际业务选择返回具体的参数
                .put("billData", tradeQueryResponse)
                .build());
    }

}
