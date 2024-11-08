package com.zzz.register.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingMaintainFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.alibaba.nacos.common.executor.NameThreadFactory;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.zzz.common.config.ServiceDefinition;
import com.zzz.common.config.ServiceInstance;
import com.zzz.common.constant.GatewayConst;
import com.zzz.register.RegisterCenter;
import com.zzz.register.RegisterListenerProcess;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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


    /**
     * 监听器处理器
     */
   private RegisterListenerProcess registerListenerProcess;



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
            namingService = NamingFactory.createNamingService(registerAddress);
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
        try {
            namingService.deregisterInstance(serviceDefinition.getServiceId(), env, serviceInstance.getIp(),
                    serviceInstance.getPort());
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    //服务的定于
    @Override
    public void subscribeAllServices(RegisterListenerProcess registerListenerProcess) {
        //1 .赋值process
        this.registerListenerProcess = registerListenerProcess;

        //2.对新添加的服务进行订阅
        doSubscribeAllServices();

        //3.开启定时任务，进行定期检查新的服务
        //可能有新服务加入，所以需要有一个定时任务来检查
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1, new NameThreadFactory(
                "doSubscribeAllServices"));

        //循环执行服务发现与订阅操作
        scheduledThreadPool.scheduleWithFixedDelay(() -> doSubscribeAllServices(), 10, 10, TimeUnit.SECONDS);
    }

    private void doSubscribeAllServices(){
        try {
            // 查出的订阅服务 和 所有服务
            Set<String> collect = namingService.getSubscribeServices().stream().map(ServiceInfo::getName).collect(Collectors.toSet());
            int pageNo = 1;
            int pageSize = 100;
            List<String> data = namingService.getServicesOfServer(pageNo, pageSize, env).getData();
            if(CollectionUtils.isEmpty(data)){
                for(String serverName:data){
                    if (collect.contains(serverName)){
                        continue;
                    }
                    //nacos事件监听器 订阅当前服务 监听这个服务
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
                                Service service = namingMaintainService.queryService(serviceName, env);

                                //得到服务定义信息
                                ServiceDefinition serviceDefinition =
                                        JSON.parseObject(service.getMetadata().get(GatewayConst.META_DATA_KEY),
                                                ServiceDefinition.class);

                                //获取服务实例信息
                                List<Instance> allInstances = namingService.getAllInstances(service.getName(), env);
                                Set<ServiceInstance> set = new HashSet<>();
                                /**
                                 * meta-data数据如下
                                 * {
                                 *   "version": "1.0.0",
                                 *   "environment": "production",
                                 *   "weight": 80,
                                 *   "region": "us-west",
                                 *   "labels": "web, primary",
                                 *   "description": "Main production service"
                                 * }
                                 */
                                for (Instance instance : allInstances) {
                                    ServiceInstance serviceInstance =
                                            JSON.parseObject(instance.getMetadata().get(GatewayConst.META_DATA_KEY),
                                                    ServiceInstance.class);
                                    set.add(serviceInstance);
                                }
                                registerListenerProcess.process(serviceDefinition, set);
                            } catch (NacosException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };

                    //当前服务之前不存在 调用监听器方法进行添加处理
                    eventListener.onEvent(new NamingEvent(serverName, null));

                    //为指定的服务和环境注册一个事件监听器
                    namingService.subscribe(serverName, env, eventListener);
                    log.info("subscribe a service ，ServiceName {} Env {}", serverName, env);

                }
            }


        } catch (NacosException e) {
            throw new RuntimeException(e);
        }

    }


}
