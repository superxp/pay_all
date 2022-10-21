package com.chaotu.pay.common.channel;

import com.alibaba.fastjson.JSONObject;
import com.chaotu.pay.dao.TChannelAccountMapper;
import com.chaotu.pay.dao.TChannelMapper;
import com.chaotu.pay.dao.TYinlianAccountMapper;
import com.chaotu.pay.po.TChannel;
import com.chaotu.pay.po.TChannelAccount;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Slf4j
public class ChannelFactory {
    private TChannelMapper channelMapper;
    private TChannelAccountMapper channelAccountMapper;
    private Map<Long,Channel> channelMap;
    private Map<Long,TChannelAccount> channelAccountMap;
    @Autowired
    public ChannelFactory(TChannelMapper channelMapper , TChannelAccountMapper channelAccountMapper, TYinlianAccountMapper yinlianAccountMapper){
        this.channelMapper = channelMapper;
        this.channelAccountMapper = channelAccountMapper;
        this.channelMap = new ConcurrentHashMap<>();
        this.channelAccountMap = new ConcurrentHashMap<>();
        registChannel(50,TongBao100Channel.class);

      /*  TChannel channel = new TChannel();
        channel.setId(32L);
        channel = channelMapper.selectOne(channel);
        YinLianChannel yinLianChannel = new YinLianChannel(channel, yinlianAccountMapper);
        channelMap.put(channel.getId(),yinLianChannel);
        channelAccountMap.put(channel.getId(),new TChannelAccount());*/

    }

    public Channel getChannel(Long id){
        return channelMap.get(id);
    }

    public TChannelAccount getChannelAccount(Long channelId){
        return channelAccountMap.get(channelId);
    }

    private void registChannel(long id,Class<? extends Channel> clzz){
        TChannel channel = new TChannel();
        channel.setId(id);
        channel = channelMapper.selectOne(channel);
        if(channel == null)
            return;
        TChannelAccount account = new TChannelAccount();
        account.setChannelId(channel.getId());
        account = channelAccountMapper.selectOne(account);

        try{
            Channel channel1 = (Channel) (clzz.getConstructor(TChannel.class, TChannelAccount.class).newInstance(channel, account));
            channelMap.put(channel.getId(),channel1);
            channelAccountMap.put(channel.getId(),account);
            log.info("通道:"+ JSONObject.toJSONString(channel)+"已注册");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
