package com.zzz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Set;

/**
 *  路由匹配规则 优先级
 *  1. 找服务ID
 *  2. 找路径匹配
 *  3. 找前缀匹配
 */
public class Rule {

    /**
     * 规则ID
     */
    Long ruleId;

    /**
     * 规则名称
     */
    String ruleName;

    /**
     * 匹配的服务名称
     */
    String serverName;

    /**
     * 负载均衡策略
     */
    private Integer loadBalancing;

    /**
     * 匹配路径
     */
    Set<String> path;

    /**
     * 前缀匹配规则
     */
    String prefix;

    /**
     * 排序规则 false 升序  true 降序
     */
    Boolean order;

    /**
     * 过滤器列表
     */
    List<Filter> list;

    /**
     * 限流规则
     */
    FlowLimiting flowLimiting;


    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Integer getLoadBalancing() {
        return loadBalancing;
    }

    public void setLoadBalancing(Integer loadBalancing) {
        this.loadBalancing = loadBalancing;
    }

    public Set<String> getPath() {
        return path;
    }

    public void setPath(Set<String> path) {
        this.path = path;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Boolean getOrder() {
        return order;
    }

    public void setOrder(Boolean order) {
        this.order = order;
    }

    public List<Filter> getList() {
        return list;
    }

    public void setList(List<Filter> list) {
        this.list = list;
    }

    public FlowLimiting getFlowLimiting() {
        return flowLimiting;
    }

    public void setFlowLimiting(FlowLimiting flowLimiting) {
        this.flowLimiting = flowLimiting;
    }
}
