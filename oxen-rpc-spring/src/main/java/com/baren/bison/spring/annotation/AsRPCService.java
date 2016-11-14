package com.baren.bison.spring.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by user on 16/10/24.
 */

@Service
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsRPCService {

    /**
     * 服务端口,必填
     * @return
     */
    int port();

    /**
     * 服务接口,如果对象只实现一个接口,可以不写
     * @return
     */
    Class serviceInterface();
}
