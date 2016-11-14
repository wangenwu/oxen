package com.baren.bison.netty.avro;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.avro.ipc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

/**
 * Created by user on 16/10/19.
 */
public class AvroNettyServer implements Server {
    private static final Logger LOG = LoggerFactory.getLogger(AvroNettyServer.class
            .getName());

    private final Responder responder;
    private final Channel serverChannel;
    private final ChannelGroup allChannels = new DefaultChannelGroup("avro-netty-server", GlobalEventExecutor.INSTANCE);

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    private final CountDownLatch closed = new CountDownLatch(1);
    private final EventExecutorGroup businessGroup;

    public AvroNettyServer(Responder responder, InetSocketAddress addr) throws IOException {
        this(responder, addr, new NioEventLoopGroup(), new NioEventLoopGroup());
    }

    public AvroNettyServer(Responder responder, InetSocketAddress addr,
                           EventLoopGroup bossGroup, EventLoopGroup workerGroup) throws IOException {
        this(responder, addr, bossGroup, workerGroup, null);
    }

    /**
     * @param businessGroup if not null, will be inserted into the Netty
     *                         pipeline. Use this when your responder does
     *                         long, non-cpu bound processing (see Netty's
     *                         EventExecutorGroup javadoc).
     * @param bossGroup  workerGroup
     *
     */
    public AvroNettyServer(Responder responder, InetSocketAddress addr,
                       EventLoopGroup bossGroup, EventLoopGroup workerGroup,
                           EventExecutorGroup businessGroup) throws IOException {
        this.responder = responder;
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.businessGroup = businessGroup;
//        EventExecutorGroup group = new DefaultEventExecutorGroup(16)
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast("frameDecoder", new AvroNettyTransportCodec.NettyFrameDecoder());
                            p.addLast("frameEncoder", new AvroNettyTransportCodec.NettyFrameEncoder());
    //                        if (executionHandler != null) {
    //                            p.addLast(executionHandler, executionHandler);
    //                        }
                            if (businessGroup != null) {
                                p.addLast(businessGroup, "handler", new AvroNettyServer.NettyServerAvroHandler());
                            } else {
                                p.addLast("handler", new AvroNettyServer.NettyServerAvroHandler());
                            }

                        }
                    })
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true);
//            if (nettyClientBootstrapOptions != null) {
//                LOG.debug("Set Netty serverBootstrap options: " +
//                        nettyClientBootstrapOptions);
//                for(Map.Entry<ChannelOption, Object> entry : nettyClientBootstrapOptions.entrySet()) {
//                    bootstrap.option(entry.getKey(), entry.getValue());
//                }
//
//            }
            ChannelFuture f = bootstrap.bind(addr).sync();
            serverChannel = f.channel();
            allChannels.add(serverChannel);
//            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Reset interrupt flag
            throw new IOException("Interrupted while bind to " + addr);
        }
    }

    @Override
    public void start() {
        // No-op.
    }

    @Override
    public void close() {
        ChannelGroupFuture future = allChannels.close();
        future.awaitUninterruptibly();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        if (businessGroup != null) {
            businessGroup.shutdownGracefully();
        }

        closed.countDown();
    }

    public void closeChannel() {
        ChannelGroupFuture future = allChannels.close();
        future.awaitUninterruptibly();
        closed.countDown();
    }

    @Override
    public int getPort() {
        return ((InetSocketAddress) serverChannel.localAddress()).getPort();
    }

    @Override
    public void join() throws InterruptedException {
        closed.await();
    }

    /**
     *
     * @return The number of clients currently connected to this server.
     */
    public int getNumActiveConnections() {
        //allChannels also contains the server channel, so exclude that from the
        //count.
        return allChannels.size() - 1;
    }



    /**
     * Avro server handler for the Netty transport
     */
    class NettyServerAvroHandler extends SimpleChannelInboundHandler<AvroNettyTransportCodec.NettyDataPack> {

        private AvroNettyTransceiver connectionMetadata = new AvroNettyTransceiver();

        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // closed on shutdown.
            allChannels.add(ctx.channel());
            super.channelActive(ctx);
        }

        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            LOG.info("Connection to {} disconnected.",
                    ctx.channel().remoteAddress());
            super.channelInactive(ctx);
            ctx.close();
            allChannels.remove(ctx.channel());
        }

        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
//            super.exceptionCaught(ctx, cause);
            LOG.warn("Unexpected exception from downstream.", cause.getCause());
            ctx.channel().close();
            allChannels.remove(ctx.channel());
        }


        @Override
        protected void channelRead0(ChannelHandlerContext ctx, AvroNettyTransportCodec.NettyDataPack msg) throws Exception {
            try {
                List<ByteBuffer> req = msg.getDatas();
                List<ByteBuffer> res = responder.respond(req, connectionMetadata);
                // response will be null for oneway messages.
                if(res != null) {
                    msg.setDatas(res);
                    ctx.channel().writeAndFlush(msg);
                }
            } catch (IOException ex) {
                LOG.warn("unexpect error");
            }
        }
    }
}
