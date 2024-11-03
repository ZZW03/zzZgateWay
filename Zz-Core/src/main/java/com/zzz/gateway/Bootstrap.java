package com.zzz.gateway;

import com.zzz.gateway.config.Config;
import com.zzz.gateway.config.ConfigLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Bootstrap {
    public static void main(String[] args) {
        // 配置文件初始化加载
        Config config = ConfigLoader.getInstance().load(args);

    }
}
