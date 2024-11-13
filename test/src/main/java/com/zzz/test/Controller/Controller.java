package com.zzz.test.Controller;

import com.zzz.test.User;
import com.zzz.test.datasource.DynamicDataSource;
import com.zzz.test.mapper.UserMapper;
import com.zzz.test.service.Userservice;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    Userservice userMapper;

    @GetMapping("test")
    public void test(){
        userMapper.get();
    }
}
