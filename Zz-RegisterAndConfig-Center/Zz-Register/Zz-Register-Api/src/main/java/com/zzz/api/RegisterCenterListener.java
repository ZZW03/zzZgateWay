package com.zzz.api;




import com.zzz.model.ServiceDefinition;
import com.zzz.model.ServiceInstance;

import java.util.List;
import java.util.Set;


/**
 * 用来监听注册中心的一些变化
 */
public interface RegisterCenterListener {

    void process(ServiceDefinition serviceDefinition,
                  List<ServiceInstance> serviceInstanceSet);
}
