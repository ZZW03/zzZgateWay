package com.zzz.model;

public class ServiceDefinition {
    /**
     * 服务名称
     */
    String ServiceName;

    /**
     * 服务环境
     */
    String env;

    public String getServiceName() {
        return ServiceName;
    }

    public String getEnv() {
        return env;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public ServiceDefinition(String serviceName, String env) {
        ServiceName = serviceName;
        this.env = env;
    }

    public ServiceDefinition() {
    }
}
