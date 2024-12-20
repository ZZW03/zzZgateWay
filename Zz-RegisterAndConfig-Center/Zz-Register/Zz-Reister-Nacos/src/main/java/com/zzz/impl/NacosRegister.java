package com.zzz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingMaintainFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.alibaba.nacos.common.executor.NameThreadFactory;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.zzz.api.RegisterCenterListener;
import com.zzz.api.RegisterCenterSever;
import com.zzz.constant.GatewayConst;
import com.zzz.model.ServiceDefinition;
import com.zzz.model.ServiceInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class NacosRegister implements RegisterCenterSever {

    private String registerAddress;


    private String env;

    /**
     * 维护实例的API
     */
    private NamingService namingService;

    /**
     * 维护服务的API
     */
    private NamingMaintainService namingMaintainService;

    @Override
    public void init(String registerAddress, String env) {
        this.registerAddress = registerAddress;
        this.env = env;
        try {
            this.namingService = NamingFactory.createNamingService(registerAddress);
            this.namingMaintainService = NamingMaintainFactory.createMaintainService(registerAddress);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        Instance instance = converInstance(serviceDefinition,serviceInstance);
        try {

            //注册
            namingService.registerInstance(serviceDefinition.getServiceName(),instance);

            //更新服务定义
            namingMaintainService.updateService(serviceDefinition.getServiceName(), serviceDefinition.getEnv(), 0,
                    Map.of("MateData", JSON.toJSONString(serviceDefinition)));
        } catch (NacosException e) {
            log.error("register fail");
            throw new RuntimeException("register fail");
        }
    }

    @Override
    public void deregister(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        Instance instance = converInstance(serviceDefinition,serviceInstance);
        try{
            namingService.deregisterInstance(serviceDefinition.getServiceName(),serviceDefinition.getEnv(),instance);

        }catch (NacosException e){
            log.error("deregister fail");
            throw new RuntimeException("deregister fail");
        }
    }

    @Override
    public void subscribeAllServices(RegisterCenterListener registerCenterListener) {

        // 进行订阅
        doSubscribeAllServices(registerCenterListener);

        //todo 开一个定时任务进行 服务进行不断的订阅
        //可能有新服务加入，所以需要有一个定时任务来检查
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1, new NameThreadFactory(
                "doSubscribeAllServices"));

        //循环执行服务发现与订阅操作 每隔一秒去发现服务 去订阅服务 保存到本地缓存中
        scheduledThreadPool.scheduleWithFixedDelay(() -> doSubscribeAllServices(registerCenterListener), 1, 1, TimeUnit.MINUTES);
    }

    private void doSubscribeAllServices(RegisterCenterListener registerCenterListener) {
        try {
            //得到当前服务已经订阅的服务
            //这里其实已经在init的时候初始化过namingservice了，所以这里可以直接拿到当前服务已经订阅的服务
            //如果不了解的可以debug
            Set<String> subscribeServiceSet =
                    namingService.getSubscribeServices().stream().map(ServiceInfo::getName).collect(Collectors.toSet());

            int pageNo = 1;
            int pageSize = 100;

            //分页从nacos拿到所有的服务列表
            List<String> serviseList = namingService.getServicesOfServer(pageNo, pageSize,env).getData();


            //拿到所有的服务名称后进行遍历
            while (CollectionUtils.isNotEmpty(serviseList)) {
                log.info("service list size {}", serviseList.size());

                for (String service : serviseList) {
                    //判断是否已经订阅了当前服务
                    if (subscribeServiceSet.contains(service)) {
                        continue;
                    }

                    //nacos事件监听器 订阅当前服务
                    //这里我们需要自己实现一个nacos的事件订阅类 来具体执行订阅执行时的操作
                    EventListener eventListener = event -> {

                        //先判断是否是注册中心事件
                        if (event instanceof NamingEvent) {
                            log.info("the triggered event info is：{}", JSON.toJSON(event));
                            NamingEvent namingEvent = (NamingEvent) event;
                            //获取当前变更的服务名
                            String serviceName = namingEvent.getServiceName();

                            try {
                                //获取服务定义信息
                                Service currentService = namingMaintainService.queryService(serviceName, env);

                                //得到服务定义信息
                                ServiceDefinition serviceDefinition = new ServiceDefinition(currentService.getName(),currentService.getGroupName());

                                //获取服务实例信息
                                List<Instance> allInstances = namingService.getAllInstances(currentService.getName(), env);
                                List<ServiceInstance> set = new ArrayList<>();

                                for (Instance instance : allInstances) {
                                    ServiceInstance serviceInstance = new ServiceInstance();
                                    serviceInstance.setUniqueId(instance.getInstanceId());
                                    serviceInstance.setServiceName(instance.getServiceName());
                                    serviceInstance.setEnable(true);
                                    serviceInstance.setIp(instance.getIp());
                                    serviceInstance.setPort(instance.getPort());
                                    serviceInstance.setHealthy(true);
                                    set.add(serviceInstance);
                                }
                                
                                //调用我们自己的订阅监听器
                                log.info("开始订阅");
                                registerCenterListener.process(serviceDefinition,set);

                            } catch (NacosException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };

                    //当前服务之前不存在 调用监听器方法进行添加处理
                    eventListener.onEvent(new NamingEvent(service, null));
                    //为指定的服务和环境注册一个事件监听器
                    namingService.subscribe(service, env, eventListener);
                    log.info("subscribe a service ，ServiceName {} Env {}", service, env);
                }
                //遍历下一页的服务列表
                serviseList = namingService.getServicesOfServer(++pageNo, pageSize, env).getData();
            }

        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }


    private Instance converInstance( ServiceDefinition serviceDefinition,ServiceInstance serviceInstance){
        Instance instance = new Instance();
        instance.setIp(serviceInstance.getIp());
        instance.setPort(serviceInstance.getPort());
        instance.setInstanceId(serviceInstance.getUniqueId());
        instance.setEnabled(serviceInstance.getEnable());
        instance.setHealthy(serviceInstance.getHealthy());
        instance.setWeight(serviceInstance.getWeight());
        instance.setMetadata(Map.of("MateData", JSON.toJSONString(serviceInstance),"ServiceDefinition",JSON.toJSONString(serviceDefinition)));
        return instance;
    }
}
