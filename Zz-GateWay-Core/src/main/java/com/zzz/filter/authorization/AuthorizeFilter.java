package com.zzz.filter.authorization;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.zzz.filter.Filter;
import com.zzz.filter.FilterAspect;
import com.zzz.model.GatewayContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
@FilterAspect(name = "authorize",id = "4",order = Integer.MAX_VALUE)
public class AuthorizeFilter implements Filter {

    private static final String SECRET_KEY = "your-256-bit-secret";

    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        log.info("进入身份验证");
        String token = ctx.getRequest().getHeaders().get("authorization");
        parseJwtToken(token);
    }

    public  void parseJwtToken(String jwt) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try{
             verifier.verify(jwt);
        }catch (JWTVerificationException e){
            log.warn("token authorize fail");
            throw new RuntimeException("身份验证失败");
        }
    }

    public  String createJwtToken() {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create()
                .withSubject("user123")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .sign(algorithm);
    }

    public static void main(String[] args) {
//        System.out.println(createJwtToken());
//
//        parseJwtToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMTIzIiwiZXhwIjoxNzMyMjc3MDEwLCJpYXQiOjE3MzIyNDEwMTB9.MqrZFZ79BkCUT9c58C6YGITCOL5J7Xw9Ls0Jjx25-nE");
    }
}
