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
import io.netty.channel.epoll.Epoll;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.socket.ServerSocketChannel;

public abstract class NettyTransportType {
    public abstract Class<? extends EventLoopGroup> getEventLoopGroupClass();
    public abstract EventLoopGroup newEventLoopGroup();
    public abstract EventLoopGroup newEventLoopGroup(int nThreads);
    public abstract Class<? extends ServerSocketChannel> getServerSocketChannelClass();
    public abstract ServerSocketChannel newServerSocketChannel();

    public static NettyTransportType getNative() {
        if (Epoll.isAvailable()) return new EpollTransportType();
        else if (KQueue.isAvailable()) return new KQueueTransportType();
        else return new NioTransportType();
    }

    public static NettyTransportType getDefault() {
        return new NioTransportType();
    }
}
