package com.zzz.config;

/**
 * 本地服务的配置
 */
public class Config {

 // ********************************************基础配置**************************************************//

    /**
     * ip地址
     */
    Integer port = 8888;

    /**
     * 服务名称
     */
    String serverName = "gateway-server";

    /**
     * 服务的唯一标识
     */
    String uniqueId = port+":"+serverName;

    /**
     * 环境
     */
    String env = "dev";


    public Config(Integer port, String serverName, String uniqueId, String env) {
        this.port = port;
        this.serverName = serverName;
        this.uniqueId = uniqueId;
        this.env = env;
    }

    public Config() {
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    @Override
    public String toString() {
        return "Config{" +
                "port=" + port +
                ", serverName='" + serverName + '\'' +
                ", uniqueId='" + uniqueId + '\'' +
                ", env='" + env + '\'' +
                '}';
    }
}
