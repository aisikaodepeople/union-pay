package com.unionpay.demo.controller;


import com.unionpay.demo.contants.PayChannelEnum;
import com.unionpay.demo.contants.RedisKey;
import com.unionpay.demo.dto.TradeDTO;
import com.unionpay.demo.exception.UnionPayException;
import com.unionpay.demo.paysupport.PayService;
import com.unionpay.demo.util.AssertUtils;
import com.unionpay.demo.util.RedisLockUtil;
import com.unionpay.demo.vo.ResultVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 支付API
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/pay")
public class PayController {

    private final ApplicationContext applicationContext;

    /**
     * 下单
     *
     * @param tradeDTO
     * @return
     * @throws Exception
     */
    @PostMapping("/trade")
    public ResultVO trade(@RequestBody @Valid TradeDTO tradeDTO) {
        AssertUtils.notNull(PayChannelEnum.getPayChannelEnum(tradeDTO.getPayChannel()), "支付渠道不支持");

        String lockKey = RedisKey.formatKey(RedisKey.lockkey_pay, tradeDTO.getOrderId());
        if (!RedisLockUtil.tryLock(lockKey, 300, 30_000)) {
            throw UnionPayException.unionPayException("请勿重复下单");
        }

        try {
            return applicationContext.getBean(tradeDTO.getPayChannel(), PayService.class).trade(tradeDTO);
        } catch (UnionPayException e) {
            log.error("下单异常:", e);
            return ResultVO.fail(e.getMessage());
        } catch (Exception e) {
            log.error("下单异常:", e);
            return ResultVO.fail("下单失败");
        } finally {
            RedisLockUtil.unlock(lockKey);
        }
    }

    /**
     * 查询账单
     *
     * @param payChannel
     * @param orderId
     * @return
     * @throws Exception
     */
    @GetMapping("/queryBill/{payChannel}/{orderId}")
    public ResultVO queryBill(@PathVariable String payChannel, @PathVariable String orderId) throws Exception {
        return applicationContext.getBean(payChannel, PayService.class).queryBill(orderId);
    }

    /**
     * 支付结果通知
     *
     * @param payChannel
     * @param orderId
     * @param request
     * @param response
     * @throws Exception
     */
    @PostMapping("/notify/{payChannel}/{orderId}")
    public void notify(@PathVariable("payChannel") String payChannel, @PathVariable("orderId") String orderId,
                       HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            applicationContext.getBean(payChannel, PayService.class).notify(orderId, request, response);
        } catch (Exception e) {
            log.error("支付回调异常:", e);
        } finally {
            PayChannelEnum payChannelEnum = PayChannelEnum.getPayChannelEnum(payChannel);
            if (payChannelEnum != null) {
                if (PayChannelEnum.isAlipay(payChannel)) {
                    response.getOutputStream().println("success");
                }
                if (PayChannelEnum.isWx(payChannel)) {
                    response.getOutputStream().println("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
                }
            }
        }
    }

}
