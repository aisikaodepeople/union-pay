package com.unionpay.demo.paysupport;

import com.unionpay.demo.dto.RefundDTO;
import com.unionpay.demo.dto.TradeDTO;
import com.unionpay.demo.vo.ResultVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 支付接口
 */
public interface PayService {

    /**
     * 下单
     *
     * @param tradeDTO
     * @return
     * @throws Exception
     */
    ResultVO trade(TradeDTO tradeDTO) throws Exception;

    /**
     * 支付成功后回调处理
     *
     * @param orderId
     * @param request
     * @param response
     * @throws Exception
     */
    void notify(String orderId, HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 退款
     *
     * @param refundDTO
     * @return
     * @throws Exception
     */
    ResultVO refund(RefundDTO refundDTO) throws Exception;

    /**
     * 查询账单
     *
     * @param orderId
     * @return
     * @throws Exception
     */
    ResultVO queryBill(String orderId) throws Exception;

}