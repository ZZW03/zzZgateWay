package com.zzz.filter.encryption;

import com.zzz.netty.client.AsyncHttpHelper;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeyPairHelper {

    private static class SingletonHolder{
        private static final KeyPairHelper INSTANCE = new KeyPairHelper();
    }

    public  static  KeyPairHelper getInstance(){
        return SingletonHolder.INSTANCE;
    }

    private KeyPair keyPair;

    public KeyPairHelper(){
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to initialize RSA key pair", e);
        }
    }
    public KeyPair getKeyPair(){
        return keyPair;
    }

}
