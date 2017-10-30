/*
 * Copyright 2017 Peter P. (JARvis PROgrammer)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.wildbot.wildbotcore.rcon.rcon.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.wildbot.wildbotcore.console.logging.Tracer;
import ru.wildbot.wildbotcore.rcon.rcon.server.packet.RconPackets;

import static io.netty.util.ReferenceCountUtil.release;

@RequiredArgsConstructor
public class InboundRconChannelHandler extends ChannelInboundHandlerAdapter {
    @NonNull private final String key;
    @NonNull private final RconPackets packets;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            System.out.println("Connection by: " + ctx.toString());
            if (msg instanceof ByteBuf) {
                final ByteBuf buf = (ByteBuf) msg;

                final byte packetId = buf.readByte();
                Tracer.info("Packet (id" + packetId + "): " + packets.get(packetId).getPacket());
            }
        } catch (IndexOutOfBoundsException e) {
            ctx.writeAndFlush(0);
        } catch (Exception e) {
            Tracer.error("An exception occurred while trying to handle RCON Packet:", e);
        } finally {
            release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Tracer.error("An exception occurred while trying to "); // TODO: 19.10.2017
        ctx.close();
    }
}
