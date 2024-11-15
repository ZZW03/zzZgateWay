package com.zzz;

import com.zzz.api.ConfigCenterSever;
import com.zzz.api.RegisterCenterListener;
import com.zzz.api.RegisterCenterSever;
import com.zzz.config.Config;
import com.zzz.config.ConfigLoader;
import com.zzz.holder.RulesHolder;
import com.zzz.holder.ServiceHolder;
import com.zzz.model.ServiceDefinition;
import com.zzz.model.ServiceInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.ServiceLoader;
import java.util.Set;

@Slf4j
public class Start {
    public static void main(String[] args) {

        //本地配置的加载
        Config config = ConfigLoader.getInStance().Load(args);

        //配置中心的加载和订阅
        ServiceLoader<ConfigCenterSever> serviceLoader = ServiceLoader.load(ConfigCenterSever.class);
        final ConfigCenterSever configCenter = serviceLoader.findFirst().orElseThrow(() -> {
            log.error("not found ConfigCenter impl");
            return new RuntimeException("not found ConfigCenter impl");
        });
        configCenter.init(config.getConfigCenterAddress(),config.getConfigCenterEnv());
        configCenter.subscribe(rules -> RulesHolder.getInstance().pullAll(rules));

        //本地服务的注册 和 订阅所有服务
        ServiceLoader<RegisterCenterSever> load = ServiceLoader.load(RegisterCenterSever.class);
        RegisterCenterSever registerCenterSever = load.findFirst().orElseThrow(() -> {
            log.error("not found RegisterCenter impl");
            return new RuntimeException("not found RegisterCenter impl");
        });
        registerCenterSever.init(config.getRegisterCenterAddress(),config.getRegisterCenterEnv());
        doRegisterAndSubScribe(config, registerCenterSever);

        //todo netty服务的启动已经过滤器链的执行


        //todo 完善工作
    }


    private static void doRegisterAndSubScribe(Config config, RegisterCenterSever registerCenterSever) {
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setServiceName(config.getServerName());
        serviceDefinition.setEnv(serviceDefinition.getEnv());
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setServiceName(config.getServerName());
        serviceInstance.setIp("127.0.0.1");
        serviceInstance.setPort(config.getPort());
        serviceInstance.setWeight(config.getWeight());
        serviceInstance.setUniqueId(config.getUniqueId());
        registerCenterSever.register(serviceDefinition, serviceInstance);

        //开始订阅
        registerCenterSever.subscribeAllServices((serviceDefinitions, serviceInstanceSet) -> ServiceHolder.getInstance().putAll(serviceDefinitions, serviceInstanceSet));

    }




}
