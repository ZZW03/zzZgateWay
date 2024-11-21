package com.zzz.httptest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Value("${server.port}")
    String port;

    @GetMapping
    public String test(){
        System.out.println("测试");
        return port;
    }

}
