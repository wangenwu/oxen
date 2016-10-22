package com.baren.bison.protocol.avro;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;

/**
 * Created by user on 16/10/13.
 */
public class AvroClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
//        pipeline.addLast(new AvroDecoder());
//        pipeline.addLast(new AvroEncoder());
        pipeline.addLast(new JsonObjectDecoder());
        pipeline.addLast(new AvroClientHandler());

    }
}
