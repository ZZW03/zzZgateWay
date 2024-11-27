package com.zzz.filter;

import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.*;

/**
 * 提供切面增强功能
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FilterAspect {

    /**
     * 过滤器ID
     */
    String id();

    /**
     * 过滤器名称
     */
    String name() default "";

    /**
     * 排序
     */
    int order() default 0;

}
