package com.zzz.test.controller;


import com.zzz.test.annotation.Retryable;
import com.zzz.test.service.Userservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class Controller {

    @Autowired
    Userservice userMapper;

    private final Map<String, DeferredResult<String>> deferredResults = new ConcurrentHashMap<>();

    @GetMapping("test")
    @Retryable
    public void test(){
        throw new RuntimeException("test");
    }


    @GetMapping("/notify")
    public DeferredResult<String> notify(@RequestParam String id) {
        DeferredResult<String> deferredResult = new DeferredResult<>(10000L); // 5秒超时
        deferredResults.put(id, deferredResult);
        deferredResult.onTimeout(() -> {
            deferredResults.remove(id);
            deferredResult.setErrorResult("Request timed out");
        });

        return deferredResult;
    }

    public void publishNotification(String id, String message) {
        DeferredResult<String> deferredResult = deferredResults.remove(id);
        if (deferredResult != null) {
            deferredResult.setResult(message);
        }
    }
}
