package com.zzz.config;

/**
 * 本地服务的配置
 */
public class Config {

 // ********************************************基础配置**************************************************//

    /**
     * ip 地址
     */
    String ip = "127.0.0.1";

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
     * 权重
     */
    Integer weight = 1;




// ********************************************注册 配制 **************************************************//

    /**
     * 配置中心的地址
     */
    String ConfigCenterAddress = "127.0.0.1:8848";

    /**
     * 注册中心的地址
     */
    String RegisterCenterAddress = "127.0.0.1:8848";

    /**
     * 配置中心环境
     */
    String ConfigCenterEnv = "dev";

    /**
     * 注册中心的配置
     */
    String RegisterCenterEnv = "dev";

    //*********************************************netty服务端***************************************************//

    /**
     * 负责连接
     */
    Integer eventLoopGroupBossNum = 1;

    /**
     * 负责处理请求
     */
    Integer eventLoopGroupWorkerNum = 1;

    /**
     * 最大接受的数据量
     */
    Integer maxLength = 64 * 1024 * 1024;


    //**************************************************netty客户端******************************************//

    Integer httpConnectTimeout = 30 * 1000;;

    Integer httpRequestTimeout = 30 * 1000;

    Integer httpMaxRequestRetry = 1 ;

    Integer httpMaxConnections = 10000;

    Integer httpConnectionsPerHost = 8000;

    Integer httpPooledConnectionIdleTimeout = 60 * 1000;


    // ********************************************基础方法**************************************************//


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getConfigCenterAddress() {
        return ConfigCenterAddress;
    }

    public void setConfigCenterAddress(String configCenterAddress) {
        ConfigCenterAddress = configCenterAddress;
    }

    public String getRegisterCenterAddress() {
        return RegisterCenterAddress;
    }

    public void setRegisterCenterAddress(String registerCenterAddress) {
        RegisterCenterAddress = registerCenterAddress;
    }

    public String getConfigCenterEnv() {
        return ConfigCenterEnv;
    }

    public void setConfigCenterEnv(String configCenterEnv) {
        ConfigCenterEnv = configCenterEnv;
    }

    public String getRegisterCenterEnv() {
        return RegisterCenterEnv;
    }

    public void setRegisterCenterEnv(String registerCenterEnv) {
        RegisterCenterEnv = registerCenterEnv;
    }

    public Integer getEventLoopGroupBossNum() {
        return eventLoopGroupBossNum;
    }

    public void setEventLoopGroupBossNum(Integer eventLoopGroupBossNum) {
        this.eventLoopGroupBossNum = eventLoopGroupBossNum;
    }

    public Integer getEventLoopGroupWorkerNum() {
        return eventLoopGroupWorkerNum;
    }

    public void setEventLoopGroupWorkerNum(Integer eventLoopGroupWorkerNum) {
        this.eventLoopGroupWorkerNum = eventLoopGroupWorkerNum;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Integer getHttpConnectTimeout() {
        return httpConnectTimeout;
    }

    public void setHttpConnectTimeout(Integer httpConnectTimeout) {
        this.httpConnectTimeout = httpConnectTimeout;
    }

    public Integer getHttpRequestTimeout() {
        return httpRequestTimeout;
    }

    public void setHttpRequestTimeout(Integer httpRequestTimeout) {
        this.httpRequestTimeout = httpRequestTimeout;
    }

    public Integer getHttpMaxRequestRetry() {
        return httpMaxRequestRetry;
    }

    public void setHttpMaxRequestRetry(Integer httpMaxRequestRetry) {
        this.httpMaxRequestRetry = httpMaxRequestRetry;
    }

    public Integer getHttpMaxConnections() {
        return httpMaxConnections;
    }

    public void setHttpMaxConnections(Integer httpMaxConnections) {
        this.httpMaxConnections = httpMaxConnections;
    }

    public Integer getHttpConnectionsPerHost() {
        return httpConnectionsPerHost;
    }

    public void setHttpConnectionsPerHost(Integer httpConnectionsPerHost) {
        this.httpConnectionsPerHost = httpConnectionsPerHost;
    }

    public Integer getHttpPooledConnectionIdleTimeout() {
        return httpPooledConnectionIdleTimeout;
    }

    public void setHttpPooledConnectionIdleTimeout(Integer httpPooledConnectionIdleTimeout) {
        this.httpPooledConnectionIdleTimeout = httpPooledConnectionIdleTimeout;
    }
}
