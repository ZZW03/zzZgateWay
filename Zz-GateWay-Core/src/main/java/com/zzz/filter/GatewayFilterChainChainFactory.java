package com.zzz.filter;

import com.alibaba.nacos.api.utils.StringUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.zzz.model.GatewayContext;
import com.zzz.model.Rule;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GatewayFilterChainChainFactory implements FilterChainFactory {

    private static class SingletonInstance {
        private static final GatewayFilterChainChainFactory INSTANCE = new GatewayFilterChainChainFactory();
    }

    public static GatewayFilterChainChainFactory getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public static void test(){

    }

    /**
     * 过滤器存储映射 过滤器id - 过滤器
     */
    private Map<String, Filter> processorFilterIdMap = new ConcurrentHashMap<>();


    private Cache<String, GatewayFilterChain> chainCache = Caffeine.newBuilder().recordStats().expireAfterWrite(10,
            TimeUnit.MINUTES).build();


    @Override
    public GatewayFilterChain buildFilterChain(GatewayContext ctx) throws Exception {
        return chainCache.get(ctx.getRule().getRuleId().toString(),k->doBuildFilterChain(ctx.getRule()));
    }

    private GatewayFilterChain doBuildFilterChain(Rule rule) {
        GatewayFilterChain gatewayFilterChain = new GatewayFilterChain();
        rule.getFilters().forEach(filter->{
            Long filterId = filter.getFilterId();
            if (filter != null){
                gatewayFilterChain.addFilter(processorFilterIdMap.get(filterId.toString()));
            }else{
                log.error("{} is null", filterId);
            }
        });
        return gatewayFilterChain;
    }

    public GatewayFilterChainChainFactory(){
        init();
    }

    public void init(){
        ServiceLoader<Filter> serviceLoader = ServiceLoader.load(Filter.class);
        serviceLoader.forEach(filter -> {
            FilterAspect annotation = filter.getClass().getAnnotation(FilterAspect.class);
            if (annotation != null) {
                //添加到过滤集合
                String filterId = annotation.id();
                if (StringUtils.isEmpty(filterId)) {
                    filterId = filter.getClass().getName();
                }
                log.info("load filter success:{},{},{},{}", filter.getClass(), annotation.id(), annotation.name(),
                        annotation.order());
                processorFilterIdMap.put(filterId, filter);
            }
        });
    }

}
