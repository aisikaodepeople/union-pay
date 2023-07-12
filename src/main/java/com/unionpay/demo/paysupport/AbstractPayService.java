package com.unionpay.demo.paysupport;


import com.unionpay.demo.config.UnionPayConfig;
import com.unionpay.demo.contants.RedisKey;
import com.unionpay.demo.dto.RefundDTO;
import com.unionpay.demo.dto.TradeDTO;
import com.unionpay.demo.exception.UnionPayException;
import com.unionpay.demo.util.AssertUtils;
import com.unionpay.demo.util.RedisLockUtil;
import com.unionpay.demo.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付公共抽象类
 */
@Slf4j
public abstract class AbstractPayService implements PayService {

    @Resource
    protected UnionPayConfig unionPayConfig;

    @Override
    public ResultVO refund(RefundDTO refundDTO) throws Exception {
        throw UnionPayException.unionPayException("没实现,哈哈~");
    }

    @Override
    public ResultVO queryBill(String orderId) throws Exception {
        throw UnionPayException.unionPayException("没实现,哈哈~");
    }

    /**
     * 当前时间
     *
     * @return
     */
    public Date currentDate() {
        return new Date();
    }

    /**
     * 支付成功回调地址(可根据实际业务调整)
     *
     * @param payChannel
     * @param orderId
     * @return
     */
    String getNotifyUrl(String payChannel, String orderId) {
        return StringUtils.join(unionPayConfig.getCallback(), "/pay/notify/", payChannel, "/", orderId);
    }

    /**
     * 金额校验
     *
     * @param remoteAmount
     * @param localAmount
     */
    void checkAmount(BigDecimal remoteAmount, BigDecimal localAmount) {
        AssertUtils.notNull(remoteAmount, "金额错误");
        AssertUtils.isTrue(remoteAmount.compareTo(localAmount) == 0, "非法金额参数");
    }

    /**
     * 下单前业务处理(可根据实际业务调整)
     *
     * @param tradeDTO
     * @param now
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void tradePreHandle(TradeDTO tradeDTO, Date now) {
        // todo 业务处理
    }

    /**
     * 下单后成功业务处理(可根据实际业务调整)
     *
     * @param orderId
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void tradeSuccessHandle(String orderId) {
        // todo 业务处理
    }

    /**
     * 支付回调成功处理(可根据实际业务做适当调整)
     *
     * @param orderId
     * @param tradeNo
     * @param amount
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void paySuccessHandle(String orderId, String tradeNo, BigDecimal amount) throws Exception {
        String redisKey = RedisKey.formatKey(RedisKey.lockkey_paysuccess, orderId);
        if (!RedisLockUtil.tryLock(redisKey, 30, 300_000)) {
            log.error("支付重复回调");
            return;
        }

        try {
            // todo 业务处理
        } finally {
            RedisLockUtil.unlock(redisKey);
        }

    }

    /**
     * 支付失败业务处理(可根据实际业务调整)
     *
     * @param orderId
     * @param msg
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void failHandle(String orderId, String msg) throws Exception {
        // todo 业务处理
    }

}
