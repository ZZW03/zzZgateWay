package com.zzz.test.annotation;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Retryable {

    Class <? extends Throwable>[] value() default {Throwable.class};

    int maxAttempts() default 3;

    long delay() default 1000;
}
