package com.zzz.filter.encryption;

import com.zzz.filter.Filter;
import com.zzz.filter.FilterAspect;
import com.zzz.model.GatewayContext;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@FilterAspect(id = "6" ,name = "encrypt",order = -1)
@Slf4j
public class EncryptFilter implements Filter {

    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        String encryptSymmetric  = ctx.getRequest().getHeaders().get("public-key");
        if (encryptSymmetric != null) {
            Jedis jedis = new Jedis("localhost",6379);
            String key = "symmetric:key";
            if (jedis.get(key) != null){
                String symmetricKey = jedis.get(key);
                resolve(ctx,symmetricKey);

            }else {
                log.info("进行解密");
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, KeyPairHelper.getInstance().getKeyPair().getPrivate());
                byte[] decryptKeyBytes = cipher.doFinal(Base64.getDecoder().decode(encryptSymmetric));
                String symmetricKey = new String(decryptKeyBytes, StandardCharsets.UTF_8);
                //应该是一个会话中 可以去redis中取 session.getId..
//                jedis.set(key, symmetricKey);
                resolve(ctx,symmetricKey);

            }
        }
    }

    private void resolve(GatewayContext ctx, String symmetricKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String uri = ctx.getRequest().getUri();
        String substring = uri.substring(9);
        SecretKeySpec keySpec = new SecretKeySpec(symmetricKey.getBytes(StandardCharsets.UTF_8), "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] encryptedBytes = Base64.getDecoder().decode(substring);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        String newUri = new String(decryptedBytes, StandardCharsets.UTF_8);


        StringBuilder stringBuilder = new StringBuilder("/encrypt/");
        stringBuilder.append(newUri);
        System.out.println(stringBuilder);

        ctx.getRequest().setUri(stringBuilder.toString());
    }

    public static void main(String[] args) throws Exception {

        //*****************************************加密堆成密钥************************************88//
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApuo3Y2d+btLhZFSgAxgjWWAR+XPknZz2NWOsMJdFYSpHQfX+22XRDxqATQqR0cM20tvGJ6IiZldYYWP33dBgBLWteHyJsL7VpNuK/S7NCPBJDuMAnrAFFdrFgXVmrvSWZiuKNsCJDHXf7a/mzo7jLLP1AeFQjdg2DiwSsO4w9LE3Ft9820ubcIqi/IKaQ8SeR7M3YGXyUP6wiY6cXVFj3tbo2GQeStLKMtMLU7HrZaLuR5mXPXpCjxQdW7DCo57w/g0qaPE/Y3JlnbK7+qGCMCmJgONxMQeymhni7x1MleZF8d3ebfv0FtFCLsV9WEiaK5zSldIjRkwdPwTfLBoLmQIDAQAB";
        String symmetricKey = "zzwshidaashuaibi";

        byte[] decoded = Base64.getDecoder().decode(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        PublicKey publicKey1 = keyFactory.generatePublic(keySpec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey1);
        byte[] encryptedSymmetricKey = cipher.doFinal(symmetricKey.getBytes());
        String encryptedSymmetricKeyBase64 = new String(Base64.getEncoder().encode(encryptedSymmetricKey));
        System.out.println(encryptedSymmetricKeyBase64);


        //******************************************************生成加密后的uri******************************************//

//        String uri = "productId=1";
//        String symmetricKey = "zzwshidaashuaibi";
//        String encryptSymmetricKey = encryptUri(uri,symmetricKey);
//        System.out.println(encryptSymmetricKey);
//
//
//        SecretKeySpec keySpec = new SecretKeySpec(symmetricKey.getBytes(StandardCharsets.UTF_8), "AES");
//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//        cipher.init(Cipher.DECRYPT_MODE, keySpec);
//        byte[] encryptedBytes = Base64.getDecoder().decode(encryptSymmetricKey);
//        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
//        String newUri = new String(decryptedBytes, StandardCharsets.UTF_8);
//
//        System.out.println(newUri);


    }


    private static String encryptUri(String uri,String symmetricKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec keySpec = new SecretKeySpec(symmetricKey.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,keySpec);
        byte[] encrypt = cipher.doFinal(uri.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypt);

    }
}

