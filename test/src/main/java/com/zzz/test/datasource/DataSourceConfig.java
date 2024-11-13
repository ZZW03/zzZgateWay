package com.zzz.test.datasource;


import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        DynamicDataSourceRouter routingDataSource = new DynamicDataSourceRouter();
        Map<Object, Object> dataSourceMap = new HashMap<>();
        try{
            if (dataSource1() != null){
                dataSourceMap.put("default", dataSource1());
                routingDataSource.setDefaultTargetDataSource(dataSource2());
            }

            if (dataSource2() != null){
                dataSourceMap.put("starrocks", dataSource2());
            }

        }catch (Exception e){

        }
        routingDataSource.setTargetDataSources(dataSourceMap);
        // 默认数据源
        return routingDataSource;
    }

    private DataSource dataSource1() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://172.16.200.53:31522/stat_mt?characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        return dataSource;
    }

    private DataSource dataSource2() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://rm-bp11b96h387z330h8.mysql.rds.aliyuncs.com:3306/vbp?characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true");
        dataSource.setUsername("haoyaouser");
        dataSource.setPassword("QhgfO_LJf2LsM5F4A_7NmWb123");
        if(isConnect(dataSource)){
            return dataSource;
        }
        return null;
    }

    private boolean isConnect(DataSource dataSource){
        try {
            Connection connection = dataSource.getConnection();
            if(connection != null){
                log.info("dataSource {} is right",dataSource.getClass().getName());
                return true;
            }
        } catch (SQLException e) {
            log.error("dataSource {} is fail",dataSource.getClass().getName());
        }

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length >= 3) {
            String callerMethodName = stackTrace[2].getMethodName();
            log.error("Called by method: {}", callerMethodName);
        }

        return false;
    }

}
