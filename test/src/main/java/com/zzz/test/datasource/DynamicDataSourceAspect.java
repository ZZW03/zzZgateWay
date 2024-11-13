package com.zzz.test.datasource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DynamicDataSourceAspect {

    @Before("@annotation(dynamicDataSource)")
    public void switchDataSource(JoinPoint point, DynamicDataSource dynamicDataSource) {
        DynamicDataSourceContextHolder.setDataSourceType(dynamicDataSource.value());

    }

    @After("@annotation(dynamicDataSource)")
    public void restoreDataSource(JoinPoint point, DynamicDataSource dynamicDataSource) {
        DynamicDataSourceContextHolder.clearDataSourceType();
    }


}

