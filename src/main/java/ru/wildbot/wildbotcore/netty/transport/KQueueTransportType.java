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

package ru.wildbot.wildbotcore.netty.transport;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class KQueueTransportType extends NettyTransportType {

    @Override
    public Class<? extends EventLoopGroup> getEventLoopGroupClass() {
        return KQueueEventLoopGroup.class;
    }

    @Override
    public EventLoopGroup newEventLoopGroup() {
        return new KQueueEventLoopGroup();
    }

    @Override
    public EventLoopGroup newEventLoopGroup(int nThreads) {
        return new KQueueEventLoopGroup(nThreads);
    }

    @Override
    public Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
        return KQueueServerSocketChannel.class;
    }

    @Override
    public ServerSocketChannel newServerSocketChannel() {
        return new KQueueServerSocketChannel();
    }
}

