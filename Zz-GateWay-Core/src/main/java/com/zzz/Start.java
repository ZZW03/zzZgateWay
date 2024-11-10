package com.zzz;

import com.zzz.config.Config;
import com.zzz.config.ConfigLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Start {
    public static void main(String[] args) {

        //本地配置的加载
        Config config = ConfigLoader.getInStance().Load(args);

        //todo 配置中心的加载和订阅


        //todo 本地服务的注册和订阅

        //todo netty服务的启动已经过滤器链的执行

        //todo 完善工作
    }


}
