package com.zzz.test.aop;

import com.zzz.test.annotation.Retryable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class RetryAspect {

    @Around("@annotation(com.zzz.test.annotation.Retryable)")
    public Object retry(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Retryable annotation = method.getAnnotation(Retryable.class);
        long delay = annotation.delay();
        int i = annotation.maxAttempts();
        Class<? extends Throwable>[] value = annotation.value();
        int attempt = 0;
        do {
            try {
                return pjp.proceed();
            } catch (Throwable e) {
                System.out.println("开始重试");
                if (!isRetryable(e, value) || ++attempt >= i) {
                    throw e;
                }
                Thread.sleep(delay);
            }
        } while (true);
    }

    private boolean isRetryable(Throwable e, Class<? extends Throwable>[] retryOn) {
        for (Class<? extends Throwable> clazz : retryOn) {
            if (clazz.isInstance(e)) {
                return true;
            }
        }
        return false;
    }
}