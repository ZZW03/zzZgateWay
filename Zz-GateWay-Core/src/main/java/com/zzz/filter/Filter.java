package com.zzz.filter;


import com.zzz.model.GatewayContext;

/**
 * Filter接口  过滤器顶层接口
 */
public interface Filter {

    void doFilter(GatewayContext ctx) throws  Exception;

    default int getOrder(){
        FilterAspect annotation = this.getClass().getAnnotation(FilterAspect.class);
        if(annotation != null){
            return annotation.order();
        }
        return Integer.MAX_VALUE;
    };
}
