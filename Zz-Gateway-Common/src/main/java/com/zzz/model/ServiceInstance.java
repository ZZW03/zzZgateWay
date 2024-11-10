package com.zzz.model;

import java.util.Map;

/**
 * 服务实例信息
 */
public class ServiceInstance {

    /**
     * Ip  地址
     */
    String Ip;

    /**
     * 端口号
     */
    Integer Port;

    /**
     * 唯一标识id
     */
    String uniqueId;

    /**
     * 所属服务名称
     */
    String ServiceName;

    /**
     * 权重
     */
    Integer weight;

    /**
     * 是否健康
     */
    Boolean isHealthy;

    /**
     * 是否启动
     */
    Boolean isEnable;



    public String getIp() {
        return Ip;
    }

    public void setIp(String ip) {
        Ip = ip;
    }

    public Integer getPort() {
        return Port;
    }

    public void setPort(Integer port) {
        Port = port;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Boolean getHealthy() {
        return isHealthy;
    }

    public void setHealthy(Boolean healthy) {
        isHealthy = healthy;
    }

    public Boolean getEnable() {
        return isEnable;
    }

    public void setEnable(Boolean enable) {
        isEnable = enable;
    }


    public ServiceInstance(String ip, Integer port, String uniqueId, String serviceName, Integer weight, Boolean isHealthy, Boolean isEnable, Map<String, String> mateData) {
        Ip = ip;
        Port = port;
        this.uniqueId = uniqueId;
        ServiceName = serviceName;
        this.weight = weight;
        this.isHealthy = isHealthy;
        this.isEnable = isEnable;
    }

    public ServiceInstance() {
    }
}
