package com.zzz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.zzz.api.ConfigCenterSever;
import com.zzz.api.subscribeProcessor;
import com.zzz.constant.GatewayConst;
import com.zzz.model.Rule;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;

import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
public class NacosConfigCenter implements ConfigCenterSever {

    String address;

    String env;

    private ConfigService configService;


    @Override
    public void init(String address, String env) {
        this.address = address;
        this.env = env;
        try {
            configService = NacosFactory.createConfigService(address);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(subscribeProcessor processor) {
        try {
            String config = configService.getConfig(GatewayConst.RULE_DATA_KEY, env, 5000);
            System.out.println(config);
            List<Rule> rules = JSON.parseObject(config).getJSONArray("rules").toJavaList(Rule.class);
            processor.process(rules);
            configService.addListener(GatewayConst.RULE_DATA_KEY, env, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }
                @Override
                public void receiveConfigInfo(String rulesInfo) {
                    List<Rule> rules = JSON.parseObject(rulesInfo).getJSONArray("rules").toJavaList(Rule.class);
                    processor.process(rules);
                }
            });
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}
