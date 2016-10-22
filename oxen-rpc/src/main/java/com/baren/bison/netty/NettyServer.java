package com.baren.bison.netty;

import com.baren.bison.protocol.avro.AvroServerInitializer;
import com.baren.bison.exception.UnsupportedProtocolException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by user on 16/10/12.
 */
public class NettyServer {


    private int port;
    private PROTOCOL protocol;

    public NettyServer(int port, String protocol) {
        this.port = port;
        this.protocol = PROTOCOL.byArgument(protocol);
        if (this.protocol == null) {
            throw new UnsupportedProtocolException("init error, un support protocol: " + protocol);
        }
    }

    public NettyServer(int port) {
        this.port = port;
        this.protocol = PROTOCOL.AVRO;

    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(this.createInitializerByProtocol())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync();

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private ChannelInitializer createInitializerByProtocol() {

        switch (this.protocol) {
            case AVRO:
                return new AvroServerInitializer();
            case HESSIAN:
                return null;
            default:
                return null;
        }
    }

    private enum PROTOCOL {
        AVRO("avro"),
        HESSIAN("hessian"),
        DEFAULT(AVRO.protocol);

        private String protocol;

        PROTOCOL(String protocol) {
            this.protocol = protocol;
        }

        public static PROTOCOL byArgument(final String arg) {
            switch (arg.toUpperCase()) {
                case "AVRO":
                    return AVRO;
                case "HESSIAN":
                    return HESSIAN;
                default:
                    return null;
            }
        }

    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new NettyServer(port).run();
    }
}