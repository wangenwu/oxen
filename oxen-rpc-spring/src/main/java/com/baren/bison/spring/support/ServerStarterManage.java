package com.baren.bison.spring.support;

import com.baren.bison.netty.avro.AvroNettyServer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.avro.ipc.specific.SpecificResponder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by user on 16/10/25.
 */
public class ServerStarterManage {


    private EventLoopGroup boss;
    private EventLoopGroup work;
    private EventExecutorGroup business;// = new DefaultEventExecutorGroup(16)

    private int defaultHandlerThreadNum = 10;

    private static ServerStarterManage manager;
    private static ReentrantLock lock = new ReentrantLock();
    private ConcurrentMap<Class, ServerStarter> allServers = new ConcurrentHashMap<>();

    private ServerStarterManage() {
        boss = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        work = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
        business = new DefaultEventExecutorGroup(defaultHandlerThreadNum);
    }

    public static ServerStarterManage getManager() {

        if (manager == null) {
            lock.lock();
            try {
                if (manager == null) {
                    manager = new ServerStarterManage();
                }
            } finally {
                lock.unlock();
            }
        }
        return manager;
    }

    public void registService(Class iface, Object impl, int port) throws IOException {
        ServerStarter s = new ServerStarter(iface, impl, port);
        this.allServers.putIfAbsent(iface, s);
        s.start();
    }

    public void closeServer() {

        for (ServerStarter s : this.allServers.values()) {
            s.stop();
        }
        this.allServers.clear();
        boss.shutdownGracefully();
        work.shutdownGracefully();
        business.shutdownGracefully();

    }

    public class ServerStarter {
        private Class iface;
        private Object impl;
        private int port;
        private AvroNettyServer nettyServer;

        public ServerStarter(Class iface, Object impl, int port) {
            this.iface = iface;
            this.impl = impl;
            this.port = port;
        }

        public void start() throws IOException {
            nettyServer = new AvroNettyServer(new SpecificResponder(this.iface, this.impl),
                    new InetSocketAddress(port), boss, work);
        }

        public void stop() {
            nettyServer.closeChannel();
        }
    }

}
