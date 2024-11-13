package com.zzz.impl;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.zzz.api.ConfigCenterSever;
import com.zzz.api.subscribeProcessor;
import lombok.extern.slf4j.Slf4j;

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

    }
}
