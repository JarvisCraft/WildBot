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
import io.netty.channel.epoll.Epoll;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.socket.ServerSocketChannel;

/**
 * Abstract class to be used for different native (machine-based) TransportTypes for Netty
 * Which does also have static {@link #getNative()} method to automatically
 * get recommended TransportType for the current machine and static {@link #getDefault()} methods
 * to get default (universal) TransportType.
 */
public abstract class NettyTransportType {

    /**
     * Gets class extending {@link EventLoopGroup} which should be used by this exact TransportType.
     * @return TransportType-special {@link EventLoopGroup} implementation
     */
    public abstract Class<? extends EventLoopGroup> getEventLoopGroupClass();

    /**
     * Creates new instance of class extending {@link EventLoopGroup}
     * which should be used by this exact TransportType.
     * @return new TransportType-special {@link EventLoopGroup} implementation's instance
     */
    public abstract EventLoopGroup newEventLoopGroup();

    /**
     * Creates new instance of class extending {@link EventLoopGroup} which should be used by this exact TransportType
     * with the specified amount of threads.
     * @param nThreads amount of threads to be used for this {@link EventLoopGroup}
     * @return new TransportType-special {@link EventLoopGroup} implementation's instance
     */
    public abstract EventLoopGroup newEventLoopGroup(int nThreads);

    /**
     * Gets class extending {@link ServerSocketChannel} which should be used by this exact TransportType.
     * @return TransportType-special {@link ServerSocketChannel} implementation
     */
    public abstract Class<? extends ServerSocketChannel> getServerSocketChannelClass();

    /**
     * Creates new instance of class extending {@link ServerSocketChannel}
     * which should be used by this exact TransportType.
     * @return new TransportType-special {@link ServerSocketChannel} implementation's instance
     */
    public abstract ServerSocketChannel newServerSocketChannel();

    /**
     * Gets the best native TransportType which is available on this machine.
     * The highest priority is given to {@link EpollTransportType}, then {@link KQueueTransportType} is checked,
     * finally default one is used if none previous are available (got using {@link #getDefault()}.
     * @return native TransportType for the machine on which the method id called
     */
    public static NettyTransportType getNative() {
        if (Epoll.isAvailable()) return new EpollTransportType();
        else if (KQueue.isAvailable()) return new KQueueTransportType();
        else return getDefault();
    }

    /**
     * Gets the universal TransportType which can be used on any machine which is {@link NioTransportType}.
     * @return default TransportType to be used on any machine
     */
    public static NettyTransportType getDefault() {
        return new NioTransportType();
    }
}
