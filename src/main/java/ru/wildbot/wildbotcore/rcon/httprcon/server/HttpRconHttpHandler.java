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
import com.google.gson.JsonSyntaxException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import lombok.*;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.io.FileUtils;
import ru.wildbot.wildbotcore.console.logging.Tracer;
import ru.wildbot.wildbotcore.rcon.httprcon.event.HttpRconEvent;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static io.netty.buffer.Unpooled.copiedBuffer;

public class HttpRconHttpHandler extends ChannelInboundHandlerAdapter {
    @NonNull @Getter private final HttpRconServerManagerSettings settings;

    private final Gson gson = new Gson();

    @Getter @Setter private String htmlErrorContent = "<html><h1>This project is using WildBot</h1>" +
            "<h2>by JARvis (Peter P.) PROgrammer</h2></html>";

    public static final String ERROR_HTML_FILE_NAME = "html/rcon/httprcon/error.html";

    public HttpRconHttpHandler(final HttpRconServerManagerSettings settings) {
        this.settings = settings;

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

            String requestContent = parseIfPossible(request);
            if (requestContent != null) {
                try {
                    HttpRconData data;
                    try {
                        data = gson.fromJson(requestContent, HttpRconData.class).decodeHashes();
                    } catch (DecoderException | JsonSyntaxException e) {
                        Tracer.info("EXce:" + e.getMessage());
                        data = null;
                    }

                    // Check if data is a valid HTTP-RCON JSON request
                    if (data == null) {
                        if (settings.isLogMalformed()) {
                            if (settings.isLogMalformedContent()) Tracer
                                    .info("Received malformed HTTP-RCON request:", requestContent);
                            else Tracer.info("Received malformed HTTP-RCON request");
                        }
                        sendErrorResponse(context, request);
                        return;
                    }

                    // Log that HTTP-RCON has been received
                    if (settings.isLogReceived()) {
                        if (settings.isLogReceivedContent()) Tracer
                                .info("Received HTTP-RCON request:", data.toString());
                        else Tracer.info("Received HTTP-RCON request");
                    }

                    // Check key
                    if (!data.verifyKey(settings.getKey())) {
                        if (settings.isLogUnauthorised()) Tracer
                                .info("Given HTTP-RCON request could not be authorised");
                        sendResponse(context, request, HttpRconResponse.getKeyHashError());
                        return;
                    }

                    // Check content if necessary
                    if (settings.isVerifyContent() && !data.verifyContent()) {
                        if (settings.isLogUnauthorised()) Tracer
                                .info("Given HTTP-RCON request's content could not be verified");
                        sendResponse(context, request, HttpRconResponse.getContentHashError());
                        return;
                    }

                    // Call event and response to request
                    Tracer.info("Calling event");
                    val event = new HttpRconEvent(data).call();
                    Tracer.info("sending response");
                    sendResponse(context, request, event.getResponse());
                } catch (Exception e) {
                    sendErrorResponse(context, request);
                }
            }

            sendErrorResponse(context, request);
        } else {
            Tracer.warn("Unexpected http-message appeared while handling RCON," +
                    "using default handling method");
            super.channelRead(context, message);
        }
    }

    private void sendResponse(final ChannelHandlerContext context, final FullHttpRequest request,
                                final HttpRconResponse rconResponse) {
        Tracer.info("Sending response for " + (rconResponse == null ? null : rconResponse.toString()));

        //Main content
        val responseJson = gson.toJson(rconResponse == null ? HttpRconResponse.getDefault() : rconResponse);

        val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK, copiedBuffer(responseJson.getBytes(StandardCharsets.UTF_8)));

        // Required headers
        if (HttpHeaders.isKeepAlive(request)) response.headers().set(HttpHeaders.Names.CONNECTION,
                HttpHeaders.Values.KEEP_ALIVE);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html;charset=utf-8");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, responseJson.length());

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
            Tracer.error("An exception occurred while trying to handle Channel exception:", e);
            context.close().addListener(future -> Tracer
                    .info("Successfully closed connection with channel which threw an exception"));
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
