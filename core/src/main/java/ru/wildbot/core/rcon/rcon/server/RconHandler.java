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

package ru.wildbot.core.rcon.rcon.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.val;
import ru.wildbot.core.console.logging.Tracer;

import java.io.StringWriter;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class RconHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final byte FAILURE = -1;
    private static final byte TYPE_RESPONSE = 0;
    private static final byte TYPE_MESSAGE = 2;
    private static final byte TYPE_LOGIN = 3;

    private final String password;

    private boolean loggedIn;

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, ByteBuf buf) {
        buf = buf.order(ByteOrder.LITTLE_ENDIAN);
        if (buf.readableBytes() < 8) return;

        val requestId = buf.readInt();
        val type = buf.readInt();

        val payloadData = new byte[buf.readableBytes() - 2];
        buf.readBytes(payloadData);
        val payload = new String(payloadData, StandardCharsets.UTF_8);

        buf.readBytes(2); // two byte padding

        if (type == TYPE_LOGIN) handleLogin(ctx, payload, requestId);
        else if (type == TYPE_MESSAGE) handleMessage(ctx, payload, requestId);
        else sendLargeResponse(ctx, requestId, "Unknown request " + Integer.toHexString(type));
    }

    private void handleLogin(ChannelHandlerContext ctx, String payload, int requestId) {
        if (password.equals(payload)) {
            loggedIn = true;
            sendResponse(ctx, requestId, TYPE_MESSAGE, "");
            Tracer.info("Rcon connection from [" + ctx.channel().remoteAddress() + "]");
        } else {
            loggedIn = false;
            sendResponse(ctx, FAILURE, TYPE_MESSAGE, "");
        }
    }

    private void handleMessage(ChannelHandlerContext ctx, String payload, int requestId) {
        if (!loggedIn) {
            sendResponse(ctx, FAILURE, TYPE_MESSAGE, "");
            return;
        }

        StringWriter message = new StringWriter();

        new RconInputMessageEvent(ctx.channel(), message, payload).call();

        sendLargeResponse(ctx, requestId, message.toString());

    }

    private void sendResponse(ChannelHandlerContext ctx, int requestId, int type, String payload) {
        ByteBuf buf = ctx.alloc().buffer().order(ByteOrder.LITTLE_ENDIAN);
        buf.writeInt(requestId);
        buf.writeInt(type);
        buf.writeBytes(payload.getBytes(StandardCharsets.UTF_8));
        buf.writeByte(0);
        buf.writeByte(0);
        ctx.write(buf);
    }

    private void sendLargeResponse(ChannelHandlerContext ctx, int requestId, String payload) {
        if (payload.isEmpty()) {
            sendResponse(ctx, requestId, TYPE_RESPONSE, "");
            return;
        }

        int start = 0;
        while (start < payload.length()) {
            val length = payload.length() - start;
            val truncated = length > 2048 ? 2048 : length;

            sendResponse(ctx, requestId, TYPE_RESPONSE, payload.substring(start, truncated));
            start += truncated;
        }
    }
}
