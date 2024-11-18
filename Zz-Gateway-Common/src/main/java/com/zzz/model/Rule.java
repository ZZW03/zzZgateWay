package com.zzz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *  路由匹配规则 优先级
 *  1. 找服务ID
 *  2. 找路径匹配
 *  3. 找前缀匹配
 */
@Builder
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
    Integer loadBalancing;

    /**
     * 匹配路径
     */
    Set<String> path;

    /**
     * 前缀匹配规则
     */
    Set<String> prefix;

    /**
     * 排序规则 false 升序  true 降序
     */
    Boolean order;

    /**
     * 过滤器列表
     */
    List<Filter> filters;

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

    public Set<String> getPrefix() {
        return prefix;
    }

    public void setPrefix(Set<String> prefix) {
        this.prefix = prefix;
    }

    public Boolean getOrder() {
        return order;
    }

    public void setOrder(Boolean order) {
        this.order = order;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> list) {
        this.filters = list;
    }

    public FlowLimiting getFlowLimiting() {
        return flowLimiting;
    }

    public void setFlowLimiting(FlowLimiting flowLimiting) {
        this.flowLimiting = flowLimiting;
    }

    public Rule(Long ruleId, String ruleName, String serverName, Integer loadBalancing, Set<String> path, Set<String> prefix, Boolean order, List<Filter> list, FlowLimiting flowLimiting) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.serverName = serverName;
        this.loadBalancing = loadBalancing;
        this.path = path;
        this.prefix = prefix;
        this.order = order;
        this.filters = list;
        this.flowLimiting = flowLimiting;
    }

    public Rule() {
    }

    public Filter getFilterConfigById(Long filterId) {
        Optional<Filter> first = filters.stream().filter(v -> v.getFilterId().equals(filterId)).findFirst();
        return first.orElse(null);
    }

    public Filter getFilterConfigByName(String name){
        return  filters.stream().filter(v->v.getFilterName().equals(name)).findFirst().orElse(null);
    }
}
