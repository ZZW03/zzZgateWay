package com.zzz.api;

public interface ConfigCenterSever {

    void init(String address,String env);

    void subscribe(subscribeProcessor processor);
}
