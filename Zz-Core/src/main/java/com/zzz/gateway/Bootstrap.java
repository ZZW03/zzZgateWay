package com.zzz.gateway;

import com.zzz.api.ConfigCenter;
import com.zzz.gateway.config.Config;
import com.zzz.gateway.config.ConfigLoader;
import com.zzz.gateway.config.DynamicConfigManager;
import lombok.extern.slf4j.Slf4j;

import java.util.ServiceLoader;

@Slf4j
public class Bootstrap {
    public static void main(String[] args) {

        // 配置文件初始化加载
        Config config = ConfigLoader.getInstance().load(args);

        //加载nacos配置中心
        ServiceLoader<ConfigCenter> serviceLoader = ServiceLoader.load(ConfigCenter.class);

        //获得这个配置中心
        final ConfigCenter configCenter = serviceLoader.findFirst().orElseThrow(() -> {
            log.error("not found ConfigCenter impl");
            return new RuntimeException("not found ConfigCenter impl");
        });

        // 从配置中心获取配置
        configCenter.init(config.getRegistryAddress(), config.getEnv());
        configCenter.subscribeRulesChange(rules -> DynamicConfigManager.getInstance()
                .putAllRule(rules));


    }
}
