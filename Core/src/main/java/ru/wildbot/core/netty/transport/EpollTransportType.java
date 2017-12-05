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

package ru.wildbot.core.netty.transport;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * {@link NettyTransportType} which can and should be used on Linux.
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class EpollTransportType extends NettyTransportType {

    /**{@inheritDoc}*/
    @Override
    public Class<? extends EventLoopGroup> getEventLoopGroupClass() {
        return EpollEventLoopGroup.class;
    }

    /**{@inheritDoc}*/
    @Override
    public EventLoopGroup newEventLoopGroup() {
        return new EpollEventLoopGroup();
    }

    /**{@inheritDoc}*/
    @Override
    public EventLoopGroup newEventLoopGroup(int nThreads) {
        return new EpollEventLoopGroup(nThreads);
    }

    /**{@inheritDoc}*/
    @Override
    public Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
        return EpollServerSocketChannel.class;
    }

    /**{@inheritDoc}*/
    @Override
    public ServerSocketChannel newServerSocketChannel() {
        return new EpollServerSocketChannel();
    }
}
