package com.zzz.register.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingMaintainFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.zzz.common.config.ServiceDefinition;
import com.zzz.common.config.ServiceInstance;
import com.zzz.common.constant.GatewayConst;
import com.zzz.register.RegisterCenter;
import com.zzz.register.RegisterCenterListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class NacosRegisterCenter implements RegisterCenter {

    /**
     * nacos 地址
     */
    private String registerAddress;

    /**
     * 环境
     */
    private String env;

    /**
     * 主要用于维护服务实例信息
     */
    private NamingService namingService;

    /**
     * 主要用于维护服务定义信息
     */
    private NamingMaintainService namingMaintainService;



    @Override
    public void init(String registerAddress, String env) {
        this.env = env;
        this.registerAddress = registerAddress;
        try{
            this.namingMaintainService = NamingMaintainFactory.createMaintainService(registerAddress);
            this.namingService = NamingFactory.createNamingService(registerAddress);
        }catch (NacosException e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    @Override
    public void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        try{
            Instance nacosInstance = new Instance();
            nacosInstance.setInstanceId(serviceInstance.getServiceInstanceId());
            nacosInstance.setPort(serviceInstance.getPort());
            nacosInstance.setIp(serviceInstance.getIp());

            nacosInstance.setMetadata(Map.of(GatewayConst.META_DATA_KEY, JSON.toJSONString(serviceInstance)));
            // 服务的唯一标识  环境
            namingService.registerInstance(serviceDefinition.getServiceId(), env, nacosInstance);

        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deregister(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {

    }

    @Override
    public void subscribeAllServices(RegisterCenterListener registerCenterListener) {

    }


    public static void main(String[] args) throws NacosException, InterruptedException {
        NamingMaintainService maintainService = NamingMaintainFactory.createMaintainService("127.0.0.1:8848");
        NamingService namingService1 = NamingFactory.createNamingService("127.0.0.1:8848");



// 第一个实例
        Instance instance1 = new Instance();
        instance1.setPort(8888);
        instance1.setIp("127.0.0.1");
        instance1.setInstanceId("instance-1"); // 设置唯一的实例 ID
        namingService1.registerInstance("123", "dev", instance1);

// 第二个实例
        Instance instance2 = new Instance();
        instance2.setPort(9999);
        instance2.setIp("126.0.0.1");
        instance2.setInstanceId("instance-2"); // 设置唯一的实例 ID
        namingService1 = NamingFactory.createNamingService("127.0.0.1:8848");
        namingService1.registerInstance("123", "dev", instance2);

// 第三个实例
        Instance instance3 = new Instance();
        instance3.setPort(7777);
        instance3.setIp("123.0.0.1");
        instance3.setInstanceId("instance-3"); // 设置唯一的实例 ID
        namingService1 = NamingFactory.createNamingService("127.0.0.1:8848");
        namingService1.registerInstance("123", "dev", instance3);


        Thread.sleep(1000000);

    }
}
