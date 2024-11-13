package com.zzz.test.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzz.test.User;
import com.zzz.test.datasource.DynamicDataSource;
import com.zzz.test.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class Userserviceimpl  extends ServiceImpl<UserMapper, User> implements Userservice{

    @Override
    public void get() {
        System.out.println(this.getById(1));
    }
}
