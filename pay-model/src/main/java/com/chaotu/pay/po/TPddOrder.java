package com.chaotu.pay.po;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "t_pdd_order")
public class TPddOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    public String getUserOrderSn() {
        return userOrderSn;
    }

    public void setUserOrderSn(String userOrderSn) {
        this.userOrderSn = userOrderSn;
    }

    /**
     * 拼多多订单号
     */
    @Column(name = "order_sn")
    private String orderSn;

    public int getSendTimes() {
        return sendTimes;
    }

    public void setSendTimes(int sendTimes) {
        this.sendTimes = sendTimes;
    }

    /**
     * 消息发送次数
     */
    @Column(name = "send_times")
    private int sendTimes;

    /**
     * 拼多多订单号
     */
    @Column(name = "user_order_sn")
    private String userOrderSn;

    /**
     * 商品id
     */
    @Column(name = "goods_id")
    private Integer goodsId;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 订单状态(0未请求,1未支付,2待发货/已支付,3已发货/未签收,4已签收/已到账,5异常)
     */
    private Byte status;

    /**
     * 商户id
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 商品数量
     */
    @Column(name = "sku_number")
    private Integer skuNumber;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 拼多多用户id
     */
    @Column(name = "pdd_user_id")
    private Integer pddUserId;

    /**
     * 用户自定义Json
     */
    private String extension;

    @Column(name = "access_token")
    private String accessToken;

    /**
     * 回调地址
     */
    @Column(name = "notify_url")
    private String notifyUrl;

    /**
     * 已回调次数
     */
    @Column(name = "notify_times")
    private Integer notifyTimes;

    public Integer getPddAccountId() {
        return pddAccountId;
    }

    public void setPddAccountId(Integer pddAccountId) {
        this.pddAccountId = pddAccountId;
    }

    /**
     * 拼多多收款账号id
     */
    @Column(name = "pdd_account_id")
    private Integer pddAccountId;


    @Column(name = "is_history")
    private int isHistory;

    public int getIsHistory() {
        return isHistory;
    }

    public void setIsHistory(int isHistory) {
        this.isHistory = isHistory;
    }

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取拼多多订单号
     *
     * @return order_sn - 拼多多订单号
     */
    public String getOrderSn() {
        return orderSn;
    }

    /**
     * 设置拼多多订单号
     *
     * @param orderSn 拼多多订单号
     */
    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    /**
     * 获取商品id
     *
     * @return goods_id - 商品id
     */
    public Integer getGoodsId() {
        return goodsId;
    }

    /**
     * 设置商品id
     *
     * @param goodsId 商品id
     */
    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    /**
     * 获取金额
     *
     * @return amount - 金额
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 设置金额
     *
     * @param amount 金额
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * 获取订单状态(0未请求,1未支付,2待发货/已支付,3已发货/未签收,4已签收/已到账,5异常)
     *
     * @return status - 订单状态(0未请求,1未支付,2待发货/已支付,3已发货/未签收,4已签收/已到账,5异常)
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置订单状态(0未请求,1未支付,2待发货/已支付,3已发货/未签收,4已签收/已到账,5异常)
     *
     * @param status 订单状态(0未请求,1未支付,2待发货/已支付,3已发货/未签收,4已签收/已到账,5异常)
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取商户id
     *
     * @return user_id - 商户id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置商户id
     *
     * @param userId 商户id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取商品数量
     *
     * @return sku_number - 商品数量
     */
    public Integer getSkuNumber() {
        return skuNumber;
    }

    /**
     * 设置商品数量
     *
     * @param skuNumber 商品数量
     */
    public void setSkuNumber(Integer skuNumber) {
        this.skuNumber = skuNumber;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新时间
     *
     * @return update_time - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取拼多多用户id
     *
     * @return pdd_user_id - 拼多多用户id
     */
    public Integer getPddUserId() {
        return pddUserId;
    }

    /**
     * 设置拼多多用户id
     *
     * @param pddUserId 拼多多用户id
     */
    public void setPddUserId(Integer pddUserId) {
        this.pddUserId = pddUserId;
    }

    /**
     * 获取用户自定义Json
     *
     * @return extension - 用户自定义Json
     */
    public String getExtension() {
        return extension;
    }

    /**
     * 设置用户自定义Json
     *
     * @param extension 用户自定义Json
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * @return access_token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @param accessToken
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * 获取回调地址
     *
     * @return notify_url - 回调地址
     */
    public String getNotifyUrl() {
        return notifyUrl;
    }

    /**
     * 设置回调地址
     *
     * @param notifyUrl 回调地址
     */
    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    /**
     * 获取已回调次数
     *
     * @return notify_times - 已回调次数
     */
    public Integer getNotifyTimes() {
        return notifyTimes;
    }

    /**
     * 设置已回调次数
     *
     * @param notifyTimes 已回调次数
     */
    public void setNotifyTimes(Integer notifyTimes) {
        this.notifyTimes = notifyTimes;
    }
}