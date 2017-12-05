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

package ru.wildbot.core.telegram.webhook;

import com.google.gson.JsonParseException;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Update;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import lombok.*;
import org.apache.commons.io.FileUtils;
import ru.wildbot.core.console.logging.Tracer;
import ru.wildbot.core.telegram.TelegramBotManager;
import ru.wildbot.core.telegram.webhook.event.TelegramUpdateEvent;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static io.netty.buffer.Unpooled.copiedBuffer;

public class TelegramWebhookHttpHandler extends ChannelInboundHandlerAdapter {
    @NonNull private final TelegramBotManager botManager;

    @Getter @Setter private String htmlErrorContent = "<html><h1>This project is using WildBot</h1>" +
            "<h2>by JARvis (Peter P.) PROgrammer</h2></html>";
    public static final String OK_RESPONSE = "ok";

    public static final String ERROR_HTML_FILE_NAME = "html/telegram/webhook/error.html";

    public TelegramWebhookHttpHandler(final TelegramBotManager botManager) {
        Tracer.info("Initialising Handler for Webhooks");

        if (botManager == null) throw new NullPointerException("No Telegram Bot Manager present");
        this.botManager = botManager;

        File errorFile = new File(ERROR_HTML_FILE_NAME);

        try {
            if (!errorFile.exists() || errorFile.isDirectory()) {
                Tracer.info("Could not find File \"vk_callback_error.html\", creating it now");

                @Cleanup val outputStream = FileUtils.openOutputStream(errorFile);
                outputStream.write(htmlErrorContent.getBytes(StandardCharsets.UTF_8));

                Tracer.info("File \"vk_callback_error.html\" has been successfully created");
            }

            val htmlLines = Files.readAllLines(errorFile.toPath());

            val htmlErrorContentBuilder = new StringBuilder();
            for (String htmlLine : htmlLines) htmlErrorContentBuilder.append(htmlLine);

            htmlErrorContent = htmlErrorContentBuilder.toString();
        } catch (IOException e) {
            Tracer.error("An exception occurred while trying to load error-HTML page", e, "Using default one");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
        if (message instanceof FullHttpRequest) {
            val request = (FullHttpRequest) message;

            val requestContent = parseIfPossibleUpdate(request);
            Update update;
            try {
                update = BotUtils.parseUpdate(requestContent);
            } catch (JsonParseException e) {
                update = null;
            }

            // If is not callback (this HTTP is ONLY FOR WEBHOOKS) then send error response
            if (update != null) {
                new TelegramUpdateEvent(update).call();
                sendOkResponse(context, request);
                return;
            }

            sendErrorResponse(context, request);
        } else {
            Tracer.warn("Unexpected http-message appeared while handling vk-callback," +
                    "using default handling method");
            super.channelRead(context, message);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        context.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        context.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                copiedBuffer(cause.getMessage().getBytes(StandardCharsets.UTF_8))));
    }

    // Gets Update (if everything OK and not confirmation)
    private String parseIfPossibleUpdate(final FullHttpRequest request) {
        if (request == null || request.getMethod() != HttpMethod.POST) return null;
        return request.content().toString(StandardCharsets.UTF_8);
    }

    private void sendOkResponse(final ChannelHandlerContext context, final FullHttpRequest request) {
        //Main content
        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK, copiedBuffer(OK_RESPONSE.getBytes(StandardCharsets.UTF_8)));

        // Required headers
        if (HttpHeaders.isKeepAlive(request)) response.headers().set(HttpHeaders.Names.CONNECTION,
                HttpHeaders.Values.KEEP_ALIVE);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html;charset=utf-8");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, OK_RESPONSE.length());

        // Write and Flush (send)
        context.writeAndFlush(response);
    }

    // Response (error)
    private void sendErrorResponse(final ChannelHandlerContext context, final FullHttpRequest request) {
        //Main content
        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK, copiedBuffer(htmlErrorContent.getBytes(StandardCharsets.UTF_8)));

        // Required headers
        if (HttpHeaders.isKeepAlive(request)) response.headers().set(HttpHeaders.Names.CONNECTION,
                HttpHeaders.Values.KEEP_ALIVE);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html;charset=utf-8");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, htmlErrorContent.length());

        // Write and Flush (send)
        context.writeAndFlush(response);
    }
}
