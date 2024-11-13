package com.zzz.test.service;

import com.zzz.test.datasource.DynamicDataSource;

public interface Userservice {

    @DynamicDataSource("starrocks")
    public void get();

}
