package com.chaotu.pay.po;

import java.math.BigDecimal;
import javax.persistence.*;

@Table(name = "t_user_rates")
public class TUserRates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 通道id
     */
    @Column(name = "channel_id")
    private Long channelId;

    public Integer getPayTypeId() {
        return payTypeId;
    }

    public void setPayTypeId(Integer payTypeId) {
        this.payTypeId = payTypeId;
    }

    /**
     * 支付id
     */
    @Column(name = "pay_type_id")
    private Integer payTypeId;

    /**
     * 商户费率：为0时走通道运营费率
     */
    private BigDecimal rate;

    /**
     * 状态：0禁用。1启用
     */
    private Byte status;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取用户id
     *
     * @return user_id - 用户id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取通道id
     *
     * @return channel_id - 通道id
     */
    public Long getChannelId() {
        return channelId;
    }

    /**
     * 设置通道id
     *
     * @param channelId 通道id
     */
    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    /**
     * 获取支付id
     *
     * @return channel_payment_id - 支付id
     */

    /**
     * 获取商户费率：为0时走通道运营费率
     *
     * @return rate - 商户费率：为0时走通道运营费率
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * 设置商户费率：为0时走通道运营费率
     *
     * @param rate 商户费率：为0时走通道运营费率
     */
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    /**
     * 获取状态：0禁用。1启用
     *
     * @return status - 状态：0禁用。1启用
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置状态：0禁用。1启用
     *
     * @param status 状态：0禁用。1启用
     */
    public void setStatus(Byte status) {
        this.status = status;
    }
}