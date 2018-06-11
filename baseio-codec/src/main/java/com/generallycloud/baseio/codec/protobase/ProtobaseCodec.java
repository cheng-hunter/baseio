/*
 * Copyright 2015-2017 GenerallyCloud.com
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.generallycloud.baseio.codec.protobase;

import java.io.IOException;

import com.generallycloud.baseio.buffer.ByteBuf;
import com.generallycloud.baseio.buffer.ByteBufAllocator;
import com.generallycloud.baseio.buffer.UnpooledByteBufAllocator;
import com.generallycloud.baseio.common.StringUtil;
import com.generallycloud.baseio.component.ChannelContext;
import com.generallycloud.baseio.component.NioSocketChannel;
import com.generallycloud.baseio.component.SocketSession;
import com.generallycloud.baseio.protocol.ChannelFuture;
import com.generallycloud.baseio.protocol.Future;
import com.generallycloud.baseio.protocol.ProtocolCodec;
import com.generallycloud.baseio.protocol.ProtocolException;

/**
 * <pre>
 *  B0 -B3  : 报文总长度        大于0:普通消息 -1:心跳PING -2:心跳PONG
 *  B4 :0   : 消息类型          0:P2P           1:BRODCAST
 *  B4 :1   : 是否包含FutureId  4 byte   
 *  B4 :2   : 是否包含SessionId 4 byte
 *  B4 :3   : 是否包含Text      4 byte
 *  B4 :4   : 是否包含Binary    4 byte
 *  B4 :5   : 预留
 *  B4 :6   : 预留
 *  B4 :7   : 预留
 *  B5      : futureNameLen
 *  .....   ：futureName
 *  .....   ：futureId,sessionId,Text,Binary
 *  
 * </pre>
 */
public class ProtobaseCodec implements ProtocolCodec {
    
    public static final int      PROTOCOL_PING   = -1;
    public static final int      PROTOCOL_PONG   = -2;

    private static final ByteBuf PING;

    private static final ByteBuf PONG;

    static {
        ByteBufAllocator allocator = UnpooledByteBufAllocator.getHeap();
        PING = allocator.allocate(4);
        PONG = allocator.allocate(4);
        PING.putInt(PROTOCOL_PING);
        PONG.putInt(PROTOCOL_PONG);
        PING.flip();
        PONG.flip();
    }

    protected int limit;

    public ProtobaseCodec() {
        this(1024 * 8);
    }

    public ProtobaseCodec(int limit) {
        this.limit = limit;
    }

    @Override
    public Future createPINGPacket(SocketSession session) {
        return new ProtobaseFutureImpl().setPING();
    }

    @Override
    public Future createPONGPacket(SocketSession session, ChannelFuture ping) {
        return ping.setPONG();
    }

    @Override
    public ChannelFuture decode(NioSocketChannel channel, ByteBuf buffer) throws IOException {
        ByteBufAllocator allocator = channel.allocator();
        ByteBuf buf = allocator.allocate(2);
        return new ProtobaseFutureImpl(buf);
    }

    @Override
    public void encode(NioSocketChannel channel, ChannelFuture future) throws IOException {
        ByteBufAllocator allocator = channel.allocator();
        if (future.isHeartbeat()) {
            ByteBuf buf = future.isPING() ? PING.duplicate() : PONG.duplicate();
            future.setByteBuf(buf);
            return;
        }
        ProtobaseFuture f = (ProtobaseFuture) future;
        String futureName = f.getFutureName();
        if (StringUtil.isNullOrBlank(futureName)) {
            throw new ProtocolException("future name is empty");
        }
        byte[] futureNameBytes = futureName.getBytes(channel.getEncoding());
        int futureNameLen = futureNameBytes.length;
        if (futureNameBytes.length > 255) {
            throw new ProtocolException("future name max length 255");
        }
        int allLen = 6 + futureNameLen;
        int textWriteSize = f.getWriteSize();
        int binaryWriteSize = f.getWriteBinarySize();
        byte h1 = 0b00000000;
        if (f.isBroadcast()) {
            h1 |= 0b10000000;
        }
        if (f.getFutureId() > 0) {
            h1 |= 0b01000000;
            allLen += 4;
        }
        if (f.getSessionId() > 0) {
            h1 |= 0b00100000;
            allLen += 4;
        }
        if (textWriteSize > 0) {
            h1 |= 0b00010000;
            allLen += 4;
            allLen += textWriteSize;
        }
        if (binaryWriteSize > 0) {
            h1 |= 0b00001000;
            allLen += 4;
            allLen += binaryWriteSize;
        }
        ByteBuf buf = allocator.allocate(allLen);
        buf.putInt(allLen - 4);
        buf.putByte(h1);
        buf.putByte((byte)futureNameLen);
        buf.put(futureNameBytes);
        if (f.getFutureId() > 0) {
            buf.putInt(f.getFutureId());
        }
        if (f.getSessionId() > 0) {
            buf.putInt(f.getSessionId());
        }
        if (textWriteSize > 0) {
            buf.putInt(textWriteSize);
        }
        if (binaryWriteSize > 0) {
            buf.putInt(binaryWriteSize);
        }
        if (textWriteSize > 0) {
            buf.put(f.getWriteBuffer(), 0, textWriteSize);
        }
        if (binaryWriteSize > 0) {
            buf.put(f.getWriteBinary(), 0, binaryWriteSize);
        }
        future.setByteBuf(buf.flip());
    }

    @Override
    public String getProtocolId() {
        return "Protobase";
    }

    @Override
    public void initialize(ChannelContext context) {}

}
