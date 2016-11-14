package com.baren.bison.spring.process;

import com.baren.bison.netty.avro.AvroNettyTransceiver;
import com.baren.bison.spring.annotation.AsRPCService;
import com.baren.bison.spring.annotation.RPCServiceClient;
import com.baren.bison.spring.exception.ClientAnnotationUseException;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;

/**
 * Created by user on 16/10/28.
 */
public class ClientServiceFieldCallback implements ReflectionUtils.FieldCallback {

    private static final Logger LOG = LoggerFactory.getLogger(ClientServiceFieldCallback.class.getName());

    private Object bean;
    public ClientServiceFieldCallback(Object bean) {
        this.bean = bean;
    }

    @Override
    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

        final RPCServiceClient serviceClient = field.getAnnotation(RPCServiceClient.class);
        Class iface = serviceClient.serviceInterface();
        ReflectionUtils.makeAccessible(field);

        int port = serviceClient.port();
        String host = serviceClient.host();
        InetSocketAddress addr = new InetSocketAddress(host, port);
        try {
            AvroNettyTransceiver client = new AvroNettyTransceiver(addr);
            Object proxy = SpecificRequestor.getClient(iface, client);
            field.set(this.bean, proxy);
        } catch (IOException e) {
            LOG.error("can't connect to addr {}", addr.toString());
            throw new ClientAnnotationUseException("RPCServiceClient supply info cant connect: " + addr.toString());
        }

    }
}
