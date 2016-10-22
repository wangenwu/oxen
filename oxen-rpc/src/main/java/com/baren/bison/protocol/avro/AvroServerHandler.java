package com.baren.bison.protocol.avro;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

/**
 * Created by user on 16/10/17.
 */
public class AvroServerHandler extends SimpleChannelInboundHandler {

//    private ChannelHandlerContext ctx;

//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        this.ctx = ctx;
//        super.channelActive(ctx);
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
        Charset utf8 = Charset.forName("UTF-8");
        String o = buffer.toString(utf8);
        System.out.println(o);

    }
}
