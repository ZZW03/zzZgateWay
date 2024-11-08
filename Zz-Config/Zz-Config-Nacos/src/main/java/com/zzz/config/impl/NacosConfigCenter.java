package com.zzz.config.impl;




import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.zzz.common.config.Rule;
import com.zzz.config.ConfigCenter;
import com.zzz.config.RulesChangeListener;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executor;


@Slf4j
public class NacosConfigCenter implements ConfigCenter {

    /**
     * 需要拉取的服务配置的DATA_ID 要求自定义
     */
    private static final String DATA_ID = "api-gateway";


    /**
     * 服务端地址
     */
    private String serverAddr;

    /**
     * 环境
     */
    private String env;

    /**
     * Nacos提供的与配置中心进行交互的接口
     */
    private ConfigService configService;

    // 初始化获得交互接口
    @Override
    public void init(String serverAddr, String env) {
        this.serverAddr = serverAddr;
        this.env = env;
        try {
            this.configService = NacosFactory.createConfigService(serverAddr);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribeRulesChange(RulesChangeListener listener) {
        try {
            //初始化通知 DATA_ID是自己定义的 返回值就是一个json
            String configJson = configService.getConfig(DATA_ID, env, 5000);
            //configJson : {"rules":[{}, {}]}
            log.info("config from nacos: {}", configJson);
            List<Rule> rules = JSON.parseObject(configJson).getJSONArray("rules").toJavaList(Rule.class);

            //第一次是加载到本地 调用我们的监听器 参数就是我们拿到的rules 然后统计到本地内存
            listener.onRulesChange(rules);

            //监听变化 对这个配置信息进行监听
            //监听器的作用就是 如果修改了rule 就重新去加载这个rule
            configService.addListener(DATA_ID, env, new Listener() {
                //是否使用额外线程执行
                @Override
                public Executor getExecutor() {
                    return null;
                }

                //这里的用法我在那片线程池动态调参的时候写到过,有兴趣可以查看博客
                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("config from nacos: {}", configInfo);
                    List<Rule> rules = JSON.parseObject(configInfo).getJSONArray("rules").toJavaList(Rule.class);
                    listener.onRulesChange(rules);
                }
            });

        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }


}
