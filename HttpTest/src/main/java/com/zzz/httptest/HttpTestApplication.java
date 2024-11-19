package com.zzz.httptest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class HttpTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(HttpTestApplication.class, args);
    }
}
