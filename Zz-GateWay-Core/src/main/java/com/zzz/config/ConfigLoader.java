package com.zzz.config;

import com.zzz.util.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.PropertiesUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class ConfigLoader {

    Config config;

    private static final ConfigLoader INSTANCE = new ConfigLoader();

    public static ConfigLoader getInStance(){
        return INSTANCE;
    }

    public Config Load(String[] args){
        this.config = new Config();

        //配置文件
        loadFromConfigFile();

        //todo 环境变量
        loadFromEnv();

        //todo jvm参数
        loadFromJvm();

        //todo 运行参数
        loadFromArgs(args);

        return this.config;
    }



    private void loadFromConfigFile() {
        InputStream in = ConfigLoader.class.getResourceAsStream("/gateway.properties");
        if(in != null){
            Properties properties = new Properties();
            try {
                properties.load(in);
            } catch (IOException e) {
                log.info("配置文件加载失败");
                throw new RuntimeException(e);
            }
            PropertiesUtils.properties2Object(properties,config);
        }else{
            log.error("配置文件没有找到");
        }
    }

    private void loadFromEnv(){

    }

    private void loadFromArgs(String[] args) {
    }

    private void loadFromJvm() {
    }
}
