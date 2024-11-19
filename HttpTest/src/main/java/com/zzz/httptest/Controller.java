package com.zzz.httptest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping
    public void test(){
        System.out.println("测试");
    }

}
