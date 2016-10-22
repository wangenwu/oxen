package com.baren.bison.protocol.avro;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * Created by user on 16/10/17.
 */
public class AvroClientHandler extends ChannelOutboundHandlerAdapter {

//    public void channelActive(ChannelHandlerContext ctx) {
//        ByteBuf msg = Unpooled.buffer(100);
////        msg.
//        ctx.writeAndFlush("{'a': 1}");
//    }


    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String str = (String) msg;
        String json = "{\"data\": "+str+"}";
        ByteBuf buffer = Unpooled.buffer(json.length());
        ByteBufUtil.writeUtf8(buffer, json);
        ctx.write(buffer, promise);

    }
}
