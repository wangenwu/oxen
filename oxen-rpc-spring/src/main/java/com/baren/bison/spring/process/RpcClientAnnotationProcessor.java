package com.baren.bison.spring.process;

import com.baren.bison.spring.annotation.RPCServiceClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * Created by user on 16/10/24.
 */
@Component
public class RpcClientAnnotationProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {


        Class bean = o.getClass();
//        if (bean.getName().endsWith("PlayController")) {
//            System.out.println(bean.getName());
//        }
        ReflectionUtils.doWithFields(bean, new ClientServiceFieldCallback(o), field -> {
            RPCServiceClient sc = field.getAnnotation(RPCServiceClient.class);
            if (sc != null) {
                return true;
            }
            return false;
        });
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {

        return o;
    }
}
