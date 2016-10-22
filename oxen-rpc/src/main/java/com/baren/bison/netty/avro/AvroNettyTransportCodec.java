package com.baren.bison.netty.avro;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.avro.AvroRuntimeException;
//import org.apache.avro.ipc.NettyTransportCodec;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 16/10/20.
 */
public class AvroNettyTransportCodec {

    /**
     * Transport protocol data structure when using Netty.
     */
    public static class NettyDataPack {
        private int serial; // to track each call in client side
        private List<ByteBuffer> datas;

        public NettyDataPack() {}

        public NettyDataPack(int serial, List<ByteBuffer> datas) {
            this.serial = serial;
            this.datas = datas;
        }

        public void setSerial(int serial) {
            this.serial = serial;
        }

        public int getSerial() {
            return serial;
        }

        public void setDatas(List<ByteBuffer> datas) {
            this.datas = datas;
        }

        public List<ByteBuffer> getDatas() {
            return datas;
        }

    }

    /**
     * Protocol encoder which converts NettyDataPack which contains the
     * Responder's output List&lt;ByteBuffer&gt; to ChannelBuffer needed
     * by Netty.
     */
    public static class NettyFrameEncoder extends MessageToByteEncoder<NettyDataPack> {


        private ByteBuffer getPackHeader(NettyDataPack dataPack) {
            ByteBuffer header = ByteBuffer.allocate(8);
            header.putInt(dataPack.getSerial());
            header.putInt(dataPack.getDatas().size());
            header.flip();
            return header;
        }

        private ByteBuffer getLengthHeader(ByteBuffer buf) {
            ByteBuffer header = ByteBuffer.allocate(4);
            header.putInt(buf.limit());
            header.flip();
            return header;
        }


        @Override
        protected void encode(ChannelHandlerContext ctx, NettyDataPack dataPack, ByteBuf out) throws Exception {
            List<ByteBuffer> origs = dataPack.getDatas();
            List<ByteBuffer> bbs = new ArrayList<ByteBuffer>(origs.size() * 2 + 1);
            bbs.add(getPackHeader(dataPack)); // prepend a pack header including serial number and list size
            for (ByteBuffer b : origs) {
                bbs.add(getLengthHeader(b)); // for each buffer prepend length field
                bbs.add(b);
            }
            ByteBuf src = Unpooled.wrappedBuffer(bbs.toArray(new ByteBuffer[bbs.size()]));
            out.writeBytes(src);
        }
    }

    /**
     * Protocol decoder which converts Netty's ChannelBuffer to
     * NettyDataPack which contains a List&lt;ByteBuffer&gt; needed
     * by Avro Responder.
     */
    public static class NettyFrameDecoder extends ByteToMessageDecoder {
        private boolean packHeaderRead = false;
        private int listSize;
        private NettyDataPack dataPack;
        private final long maxMem;
        private static final long SIZEOF_REF = 8L; // mem usage of 64-bit pointer


        public NettyFrameDecoder() {
            maxMem = Runtime.getRuntime().maxMemory();
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

            if (!packHeaderRead) {
                if (decodePackHeader(ctx, ctx.channel(), in)) {
                    packHeaderRead = true;
                }
                return;
            } else {
                if (decodePackBody(ctx, ctx.channel(), in)) {
                    packHeaderRead = false; // reset state
                    out.add(dataPack);
//                    return dataPack;
                } else {
                    return;
                }
            }

        }

//        /**
//         * decode buffer to NettyDataPack
//         */
//        @Override
//        protected Object decode(ChannelHandlerContext ctx, Channel channel,
//                                ChannelBuffer buffer) throws Exception {
//
//            if (!packHeaderRead) {
//                if (decodePackHeader(ctx, channel, buffer)) {
//                    packHeaderRead = true;
//                }
//                return null;
//            } else {
//                if (decodePackBody(ctx, channel, buffer)) {
//                    packHeaderRead = false; // reset state
//                    return dataPack;
//                } else {
//                    return null;
//                }
//            }
//
//        }

        private boolean decodePackHeader(ChannelHandlerContext ctx, Channel channel,
                                         ByteBuf buffer) throws Exception {
            if (buffer.readableBytes()<8) {
                return false;
            }

            int serial = buffer.readInt();
            int listSize = buffer.readInt();

            // Sanity check to reduce likelihood of invalid requests being honored.
            // Only allow 10% of available memory to go towards this list (too much!)
            if (listSize * SIZEOF_REF > 0.1 * maxMem) {
                channel.close().await();
                throw new AvroRuntimeException("Excessively large list allocation " +
                        "request detected: " + listSize + " items! Connection closed.");
            }

            this.listSize = listSize;
            dataPack = new NettyDataPack(serial, new ArrayList<ByteBuffer>(listSize));

            return true;
        }

        private boolean decodePackBody(ChannelHandlerContext ctx, Channel channel,
                                       ByteBuf buffer) throws Exception {
            if (buffer.readableBytes() < 4) {
                return false;
            }

            buffer.markReaderIndex();

            int length = buffer.readInt();

            if (buffer.readableBytes() < length) {
                buffer.resetReaderIndex();
                return false;
            }

            ByteBuffer bb = ByteBuffer.allocate(length);
            buffer.readBytes(bb);
            bb.flip();
            dataPack.getDatas().add(bb);

            return dataPack.getDatas().size()==listSize;
        }

    }


}
