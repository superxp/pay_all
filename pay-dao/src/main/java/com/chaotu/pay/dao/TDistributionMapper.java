package com.chaotu.pay.dao;

import com.chaotu.pay.po.TDistribution;
import com.chaotu.pay.po.TWithdraws;
import com.chaotu.pay.utils.MyMapper;
import com.chaotu.pay.vo.DistributionVo;
import com.chaotu.pay.vo.WithdrawsVo;

import java.util.List;
import java.util.Map;

public interface TDistributionMapper extends MyMapper<TDistribution> {
    /**
     * 获取所有结算记录
     * @param withdrawsVo
     * @return
     */
    List<TDistribution> findAll(DistributionVo withdrawsVo);

    /**
     * 获取结算总账目
     * @param withdrawsVo
     * @return
     */
    Map<String, Object> getGeneralAccount(DistributionVo withdrawsVo);
}