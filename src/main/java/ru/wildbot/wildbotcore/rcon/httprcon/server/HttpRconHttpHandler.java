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

package ru.wildbot.wildbotcore.rcon.httprcon.server;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import ru.wildbot.wildbotcore.WildBotCore;
import ru.wildbot.wildbotcore.console.logging.Tracer;
import ru.wildbot.wildbotcore.rcon.httprcon.event.HttpRconEvent;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import static io.netty.buffer.Unpooled.copiedBuffer;

public class HttpRconHttpHandler extends ChannelInboundHandlerAdapter {
    @NonNull private final String key;

    private final Gson gson = new Gson();

    @Getter @Setter private String htmlErrorContent = "<html><h1>This project is using WildBot</h1>" +
            "<h2>by JARvis (Peter P.) PROgrammer</h2></html>";
    public static final String OK_RESPONSE = "ok";

    public static final String ERROR_HTML_FILE_NAME = "html/rcon/httprcon/error.html";

    public HttpRconHttpHandler(final String key) {
        Tracer.info("Initialising Handler for RCON");

        if (key == null) throw new NullPointerException("No confirmation code present");
        this.key = key;

        File errorFile = new File(ERROR_HTML_FILE_NAME);

        try {
            if (!errorFile.exists() || errorFile.isDirectory()) {
                Tracer.info("Could not find File \"error.html\", creating it now");

                @Cleanup val outputStream = FileUtils.openOutputStream(errorFile);
                outputStream.write(htmlErrorContent.getBytes(StandardCharsets.UTF_8));

                Tracer.info("File \"error.html\" has been successfully created");
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

            Tracer.info("RCON-channel connection received");

            String requestContent = parseIfPossible(request);
            if (requestContent != null) {
                try {
                    val data = gson.fromJson(requestContent, HttpRconData.class).decodeHashes();
                    Tracer.info("GSONed: " + data.toString());
                    Tracer.info(data.verify(key));
                } catch (Exception e) {
                    sendErrorResponse(context, request);
                }
                /*
                int separatorIndex1 = requestContent.indexOf(":");
                int separatorIndex2 = requestContent.indexOf(":", separatorIndex1 + 1);
                if (separatorIndex1 >= 0 && separatorIndex2 > 0) {
                    if (requestContent.substring(0, separatorIndex1).equals(key)) {
                        val name = requestContent.substring(separatorIndex1 + 1, separatorIndex2);
                        val data = requestContent.substring(separatorIndex2 + 1);

                        try {
                            val event = new HttpRconEvent(name, data);
                            WildBotCore.getInstance().getEventManager().callEvents(event);
                            sendOkResponse(context, request, event.getHtmlResponse());
                            return;
                        } catch (Exception e) {
                            Tracer.error("An exception occurred while trying to call to HttpRconEvent:", e);
                        }
                    } else Tracer.info("Wrong first given for RCON-request: \"" + requestContent
                            + "\" expected \"" + key + "\"");
                }
                */
            }

            sendErrorResponse(context, request);
        } else {
            Tracer.warn("Unexpected http-message appeared while handling RCON," +
                    "using default handling method");
            super.channelRead(context, message);
        }
    }

    // Response (confirmation code)
    private void sendOkResponse(final ChannelHandlerContext context, final FullHttpRequest request,
                                final String htmlResponse) {
        //Main content
        val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK, copiedBuffer(htmlResponse.getBytes(StandardCharsets.UTF_8)));

        // Required headers
        if (HttpHeaders.isKeepAlive(request)) response.headers().set(HttpHeaders.Names.CONNECTION,
                HttpHeaders.Values.KEEP_ALIVE);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html;charset=utf-8");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, htmlResponse.length());

        // Write and Flush (send)
        context.writeAndFlush(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) throws Exception {
        context.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        try {
            context.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    copiedBuffer(cause.getMessage().getBytes(StandardCharsets.UTF_8))));
        } catch (Exception e) {
            Tracer.info("context: " + context);
            Tracer.info("cause: " + cause);
            Tracer.info("cause: " + Arrays.toString(cause.getStackTrace()));
        }
    }

    // Gets Callback (if everything OK and not confirmation)
    private String parseIfPossible(final FullHttpRequest request) {
        if (request == null || request.getMethod() != HttpMethod.POST) return null;
        return request.content().toString(StandardCharsets.UTF_8);
    }

    // Response (error)
    private void sendErrorResponse(final ChannelHandlerContext context, final FullHttpRequest request) {
        //Main content
        val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
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
