package com.chaotu.pay.service;

import com.chaotu.pay.po.TDistribution;
import com.chaotu.pay.po.TWithdraws;
import com.chaotu.pay.vo.DistributionVo;
import com.chaotu.pay.vo.PageVo;
import com.chaotu.pay.vo.SearchVo;
import com.chaotu.pay.vo.WithdrawsVo;

import java.text.ParseException;
import java.util.Map;

/**
 * @description:
 * @author: chenyupeng
 * @create: 2019-04-19 11:11
 **/

public interface DistributionService {
    Map<String, Object> findByCondition(PageVo pageVo, SearchVo searchVo, DistributionVo withdrawsVo) throws ParseException;
    void add(DistributionVo vo);
    void update(TDistribution withdraws);

    void pass(TDistribution vo);
}
