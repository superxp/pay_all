package com.chaotu.pay.service.impl;

import com.chaotu.pay.dao.TDistributionMapper;
import com.chaotu.pay.dao.TWithdrawsMapper;
import com.chaotu.pay.po.TDistribution;
import com.chaotu.pay.po.TWallet;
import com.chaotu.pay.po.TWithdraws;
import com.chaotu.pay.service.DistributionService;
import com.chaotu.pay.service.UserService;
import com.chaotu.pay.service.WalletService;
import com.chaotu.pay.service.WithdrawsService;
import com.chaotu.pay.vo.*;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 结算管理
 * @author: chenyupeng
 * @create: 2019-04-19 11:11
 **/
@Service
public class DistributionServiceImpl implements DistributionService {
    @Autowired
    private UserService userService;
    @Autowired
    private TDistributionMapper tWithdrawsMapper;
    @Autowired
    private WalletService walletService;
    @Override
    public Map<String, Object> findByCondition(PageVo pageVo, SearchVo searchVo, DistributionVo withdrawsVo) {
        Example example = new Example(TDistribution.class);
        Example.Criteria criteria = example.createCriteria();


        //通过时间查询所有订单
        //如果时间为空，则为当日00:00:00至当前时间
        if(!StringUtils.isEmpty(searchVo.getStartDate())){
            withdrawsVo.setStartTime(searchVo.getStartDate());
        }

        if(!StringUtils.isEmpty(searchVo.getEndDate())){
            withdrawsVo.setEndTime(searchVo.getStartDate());
        }

       /* try {*/

        UserVo userVo = userService.currentUser();
        String userId = userVo.getId();
        if(!"682265633886208".equals(userId)){
            example.createCriteria().andEqualTo("userId",userId);
            withdrawsVo.setUserId(userId);
        }
        PageHelper.startPage(pageVo.getPageNumber(), pageVo.getPageSize());
        List<TDistribution> withdrawsList = tWithdrawsMapper.findAll(withdrawsVo);


            int count = tWithdrawsMapper.selectCountByExample(example);

            Map<String,Object> generalAccount = tWithdrawsMapper.getGeneralAccount(withdrawsVo);
            if(generalAccount == null){
                generalAccount = new HashMap<>();

            }
            generalAccount.put("allcount", count/*=nullwithdrawsList.size()*/);
            Map<String,Object> map = new HashMap<>();

            MyPageInfo info = new MyPageInfo(withdrawsList);
            if(!CollectionUtils.isEmpty(withdrawsList)){
                info.setTotal(count);
                info.setPageNum(pageVo.getPageNumber());
            }

            map.put("pageInfo", info);
            map.put("generalAccount", generalAccount);
            return map;
     /*   } catch (Exception e) {
            throw new BizException(ExceptionCode.UNKNOWN_ERROR);
        }*/

    }

    @Override
    public void add(DistributionVo vo) {
        TDistribution withdraws = new TDistribution();
        TWallet w = new TWallet();
        UserVo userVo = userService.currentUser();
        String userId = userVo.getId();
        w.setUserId(userId);
        w.setType("2");
        TWallet wallet = walletService.selectOne(w);
        BigDecimal residualAmount = wallet.getResidualAmount();
        BigDecimal rate = new BigDecimal(5);
        if (!org.apache.commons.lang3.StringUtils.equals(userId,"682265633886208"))
            throw new IllegalArgumentException("余额不足");

        BeanUtils.copyProperties(vo,withdraws);
        withdraws.setWithdrawrate(rate);
        withdraws.setToamount(withdraws.getWithdrawamount().subtract(rate));
        withdraws.setStatus((byte)0);
        withdraws.setCreateBy(userId);
        withdraws.setCreateTime(new Date());
        withdraws.setUserId(userId);
        withdraws.setAccountname(vo.getAccountname());
        //walletService.editAmount(wallet,withdraws.getWithdrawamount().add( new BigDecimal(5)).toString(),"1");
        tWithdrawsMapper.insert(withdraws);
    }

    @Override
    public void update(TDistribution withdraws) {
        tWithdrawsMapper.updateByPrimaryKey(withdraws);
    }

    @Override
    public void pass(TDistribution vo) {
        TDistribution withdraws = tWithdrawsMapper.selectByPrimaryKey(vo);
        withdraws.setStatus((byte)2);
        TWallet w = new TWallet();
        w.setUserId(vo.getUserId());
        w.setType("2");
        //walletService.editAmount(w,withdraws.getWithdrawamount().toString(),"1");
        tWithdrawsMapper.updateByPrimaryKey(withdraws);
    }
}
