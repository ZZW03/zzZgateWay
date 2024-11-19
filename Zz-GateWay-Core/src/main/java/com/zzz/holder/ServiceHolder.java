package com.zzz.holder;

import com.zzz.model.ServiceDefinition;
import com.zzz.model.ServiceInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class ServiceHolder {

    private static class  SingletonHolder{
        private static final ServiceHolder instance = new ServiceHolder();
    }

    public static ServiceHolder getInstance(){
        return SingletonHolder.instance;
    }

    /**
     * 服务-服务名
     */
    Map<String, ServiceDefinition> ServiceMap = new ConcurrentHashMap<>();

    /**
     * 服务-实例
     */
    Map<String, List<ServiceInstance>> ServiceInstanceMap = new ConcurrentHashMap<>();

    /**
     * 实例-实例id
     */
    Map<String,ServiceInstance> uniqueIdInstanceMap = new ConcurrentHashMap<>();


    public void putAll(ServiceDefinition serviceDefinition, List<ServiceInstance> serviceInstances){
        if (serviceDefinition == null || serviceInstances == null){
            return;
        }

        log.info("开始存储规则");
        ServiceMap.put(serviceDefinition.getServiceName(), serviceDefinition);
        ServiceInstanceMap.put(serviceDefinition.getServiceName(), serviceInstances);
        serviceInstances.forEach(v->{
            uniqueIdInstanceMap.put(v.getUniqueId(), v);
        });
    }

    public void putServiceDefinition(String name ,ServiceDefinition serviceDefinition){
        ServiceMap.put(name, serviceDefinition);
    }

    public void putServiceInstance(String uniqueId ,ServiceInstance serviceInstance){
        uniqueIdInstanceMap.put(uniqueId, serviceInstance);
    }

    public void putServiceInstance(String Name,List<ServiceInstance> set){
        ServiceInstanceMap.put(Name,new ArrayList<>(ServiceInstanceMap.get(Name)));
    }

    public ServiceDefinition getServiceDefinitionByName(String serviceName){
        ServiceDefinition serviceDefinition = null;
        try{
             serviceDefinition = ServiceMap.get(serviceName);
        }catch (Exception e){
            log.error(e.getMessage());
        }

        return serviceDefinition;
    }

    public List<ServiceInstance> getServiceInstancesByServiceName(String serviceName){
        return ServiceInstanceMap.get(serviceName);
    }

    public ServiceInstance getServiceInstanceById(String serviceName){
        return uniqueIdInstanceMap.get(serviceName);
    }

    public void deleteServiceInstance(String uniqueId){
        uniqueIdInstanceMap.remove(uniqueId);
    }

    public void deleteServiceDefinition(String serviceName){
        ServiceMap.remove(serviceName);
    }

    public void deleteSetInstance(String serviceName){
        ServiceInstanceMap.remove(serviceName);
    }

    public void clearAll(){
        ServiceMap.clear();
        ServiceInstanceMap.clear();
        uniqueIdInstanceMap.clear();
    }

}
