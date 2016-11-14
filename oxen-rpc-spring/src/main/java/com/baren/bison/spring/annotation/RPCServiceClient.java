package com.baren.bison.spring.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by user on 16/10/24.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RPCServiceClient {
    /**
     * 服务端口,必填
     * @return
     */
    int port();

    /**
     * 服务host地址
     * @return
     */
    String host() default "";

    /**
     * 服务接口,如果对象只实现一个接口,可以不写
     * @return
     */
    Class serviceInterface();
}
