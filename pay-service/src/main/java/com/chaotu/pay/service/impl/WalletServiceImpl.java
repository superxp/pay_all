package com.chaotu.pay.service.impl;

import com.chaotu.pay.dao.TWalletMapper;

import com.chaotu.pay.po.*;

import com.chaotu.pay.service.WalletService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class WalletServiceImpl implements WalletService {


    @Autowired
    private TWalletMapper tWalletMapper;

    @Override
    public TWallet selectOne(TWallet wallet) {
        return tWalletMapper.selectOne(wallet);

    }

    @Override
    public synchronized int editAmount(TWallet wallet, String amount, String option) {
        switch(option){
            case "0":

                List<TWallet> wallets = tWalletMapper.select(wallet);
                if (wallets.size() > 0) {
                    TWallet wallet1 = wallets.get(0);
                    BigDecimal amountD = new BigDecimal(amount);
                    wallet1.setResidualAmount(wallet1.getResidualAmount().add(amountD));  //增加
                    tWalletMapper.updateByPrimaryKey(wallet1);
                    return 1;
                }

                break;
            case "1":
                List<TWallet> wallets2 = tWalletMapper.select(wallet);
                if(wallets2.size()>0){
                    TWallet wallet1 = wallets2.get(0);
                    BigDecimal amountD = new BigDecimal(amount);
                    if(wallet1.getResidualAmount().compareTo(amountD)>=0){
                        wallet1.setResidualAmount(wallet1.getResidualAmount().subtract(amountD)); //减少
                        tWalletMapper.updateByPrimaryKey(wallet1);
                        return 1;
                    }else{
                        return -1;
                    }


                }
                break;
        }
        return 0;
    }

    @Override
    public void add(TWallet wallet) {
        tWalletMapper.insert(wallet);
    }
}
