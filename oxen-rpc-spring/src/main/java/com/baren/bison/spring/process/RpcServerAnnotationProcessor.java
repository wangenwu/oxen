package com.baren.bison.spring.process;

import com.baren.bison.spring.annotation.AsRPCService;
import com.baren.bison.spring.exception.ServiceAnnotationUseException;
import com.baren.bison.spring.support.ServerStarterManage;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by user on 16/10/24.
 */
@Component
public class RpcServerAnnotationProcessor implements ApplicationContextAware, InitializingBean, ApplicationListener {

    private ApplicationContext applicationContext;
    private ServerStarterManage starter = ServerStarterManage.getManager();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    /**
     *
     * ServiceAnnotationUseException
     *
     */
    public void afterPropertiesSet() throws Exception {

        final Map<String, Object> services = applicationContext.getBeansWithAnnotation(AsRPCService.class);
        if (services != null) {
            for (final Object rpcInstance : services.values()) {
                final Class<? extends Object> serverClass = rpcInstance.getClass();
                final AsRPCService rpcService = serverClass.getAnnotation(AsRPCService.class);
                int port = rpcService.port();
                Class sicClazz = rpcService.serviceInterface();
                if (port == 0) {
                    throw new IllegalArgumentException("rpc service port not null");
                }
                Class<? extends Object> serviceInterface;
                if (sicClazz == null) {
                    Class<? extends Object>[] allInter = serverClass.getInterfaces();
                    if (allInter != null && allInter.length ==1) {
                        serviceInterface = allInter[0];
                    } else {
                        throw new ServiceAnnotationUseException("AsRPCService annotation use error, must specify a interface " +
                                "or implement only one interface");
                    }
                } else {
                    serviceInterface = sicClazz;
                }

                starter.registService(serviceInterface, rpcInstance, port);

            }
        }


    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextClosedEvent) {
            starter.closeServer();
        }
    }
}
