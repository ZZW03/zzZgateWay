//package com.zzz.config;
//
//import jakarta.annotation.PostConstruct;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.security.Key;
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.NoSuchAlgorithmException;
//
//@Configuration
//public class SecurityConfig {
//
//    private KeyPair keyPair;
//
//    @PostConstruct
//    public void init(){
//        KeyPairGenerator keyPairGenerator;
//        try {
//            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//            keyPairGenerator.initialize(2048);
//            keyPair = keyPairGenerator.generateKeyPair();
//        }catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("Failed to initialize RSA key pair", e);
//        }
//    }
//
//    public KeyPair getKeyPair() {
//        return keyPair;
//    }
//
//}
