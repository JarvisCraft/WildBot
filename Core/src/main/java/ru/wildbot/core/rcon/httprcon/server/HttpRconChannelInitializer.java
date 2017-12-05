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

package ru.wildbot.core.rcon.httprcon.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.wildbot.core.console.logging.Tracer;

@RequiredArgsConstructor
public class HttpRconChannelInitializer extends ChannelInitializer {
    @NonNull private final HttpRconServerManagerSettings settings;

    @Override
    protected void initChannel(Channel channel) {
        Tracer.info("Initialising channel for Http-RCON handling");
        // Codec -> Aggregator -> Confirmation -> Callback
        channel.pipeline().addLast("codec", new HttpServerCodec());
        channel.pipeline().addLast("aggregator", new HttpObjectAggregator(524288)); // 2^19
        channel.pipeline().addLast("http_rcon", new HttpRconHttpHandler(settings));
    }
}
