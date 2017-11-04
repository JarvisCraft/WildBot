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

package ru.wildbot.wildbotcore.netty;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import ru.wildbot.wildbotcore.api.manager.WildBotManager;
import ru.wildbot.wildbotcore.console.logging.Tracer;
import ru.wildbot.wildbotcore.core.exception.NotImplementedException;
import ru.wildbot.wildbotcore.netty.transport.NettyTransportType;
import ru.wildbot.wildbotcore.util.collection.Pair;

import java.util.HashSet;

@AllArgsConstructor
public class NettyServerCore implements WildBotManager {
    @NonNull private final NettyServerCoreSettings settings;

    @Getter private NettyTransportType transportType;

    @Getter private boolean enabled = false;

    private EventLoopGroup parentGroup;
    private EventLoopGroup childGroup;

    // Each name can associate with multiple Pairs of Channel and it's Port
    private final Multimap<String, Pair<ChannelFuture, Integer>> channels = Multimaps
            .synchronizedListMultimap(ArrayListMultimap.create());

    private Thread shutdownHook = new Thread(() -> {
        try {
            shutdown();
        } catch (Exception e) {
            Tracer.error("An exception occurred while trying to disable Netty-Server-core " +
                    "while Shutting Down Runtime:", e);
        }
    });

    @Override
    public void enable() throws Exception {
        checkEnabled();

        if (settings.isUseNative()) transportType = NettyTransportType.getNative();
        else transportType = NettyTransportType.getDefault();

        if (settings.isLog()) Tracer.info("Using " + transportType.getClass().getSimpleName()
                + " for Netty ServerCore");
        // Parent (boss) and Child (worker) groups
        parentGroup = transportType.newEventLoopGroup(settings.getParentThreads());
        childGroup = transportType.newEventLoopGroup(settings.getChildThreads());

        // Hook to safe-stop netty in case of process being shut-down
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        enabled = true;
    }

    @Override
    public void disable() throws Exception {
        checkDisabled();

        Runtime.getRuntime().removeShutdownHook(shutdownHook);

        shutdown();

        parentGroup = null;
        childGroup = null;

        transportType = null;

        channels.clear();

        enabled = false;
    }

    public NettyServerCore(final NettyServerCoreSettings settings) {
        this.settings = settings;
    }

    public NettyServerCore(final int parentThreads, final int childThreads, final boolean useNative,
                           final boolean log) {
        this(new NettyServerCoreSettings(parentThreads, childThreads, useNative, log));
    }

    public NettyServerCore(final int parentThreads, final int childThreads, final boolean useNative) {
        this(new NettyServerCoreSettings(parentThreads, childThreads, useNative, true));
    }

    public NettyServerCore(final int parentThreads, final int childThreads) {
        this(parentThreads, childThreads, true);
    }

    public NettyServerCore() {
        this(0, 0);
    }

    public void open(final String name, final ServerBootstrap bootstrap, final int port) throws Exception {
        if (settings.isLog())  Tracer.info("Opening Netty Channel for name `" + name + "`");

        // Add parent and children for Bootstrap if none
        if (bootstrap.group() == null || bootstrap.childGroup() == null) bootstrap.group(parentGroup, childGroup);

        // Registering in `channels` Map
        channels.put(name, Pair.of(bootstrap.bind(port), port));

        if (settings.isLog()) Tracer.info("Netty Channel for name `" + name
                + "` has been successfully opened");
    }

    public boolean close(final String name, final int port) throws Exception {
        //if (true) throw new NotImplementedException("Channel closing is yet disabled");
        if (settings.isLog()) Tracer.info("Closing Netty Channel for name `" + name + "` and port " + port);

        if (channels.containsKey(name)) {
            for (val channel : channels.get(name))
                if (channel.getSecond() == port) {
                    if (settings.isLog()) Tracer.info("Closing Netty Channel on port " + channel.getSecond());

                            /*
                            .channel().close().addListener(future -> {
                                if (future.isSuccess()) {
                                    Tracer.info("Netty Channel on port " + channel.getSecond()
                                            + " has been successfully stopped");
                                    channels.remove(name, channel);// TODO: 29.10.2017 rework

                                    Tracer.info("Netty Channel for name `" + name + "` on port " + port
                                            + " has been successfully stopped");
                                } else {
                                    Tracer.error("An exception occurred while closing channel on port "
                                            + channel.getSecond() + ":", future.cause());
                                    if (future.cause() != null) future.cause().printStackTrace();
                                }
                            }).awaitUninterruptibly();
                            */

                            channel.getFirst()
                                    .channel().close().addListener(future -> Tracer.info("close")).awaitUninterruptibly()
                                    .channel().disconnect().addListener(future -> Tracer.info("disconnect")).awaitUninterruptibly()
                                    .channel().deregister().addListener(future -> Tracer.info("deregister")).awaitUninterruptibly();

                    Tracer.info("END");
                    return true;
                }
        }

        if (settings.isLog()) Tracer.info("There is no registered Netty Channel for name `" + name
                + "` on port " + port + " to be stopped");

        return false;
    }

    public boolean close(final String name) throws Exception {
        //if (true) throw new NotImplementedException("Channel closing is yet disabled");

        if (settings.isLog()) Tracer.info("Closing all Netty Channels for name `" + name + "`");

        if (channels.containsKey(name)) {
            val removes = new HashSet<Pair<String, Pair<ChannelFuture, Integer>>>();

            for (Pair<ChannelFuture, Integer> channel : this.channels.get(name)) {
                if (settings.isLog()) Tracer.info("Closing Netty Channel on port " + channel.getSecond());

                // TODO: 01.11.2017

                if (settings.isLog()) Tracer.info("Netty Channel on port " + channel.getSecond()
                        + " has been successfully stopped");

                removes.add(Pair.of(name, channel));
            }

            for (val remove : removes) channels.remove(remove.getFirst(), remove.getSecond());

            if (settings.isLog()) Tracer.info("All Netty Channels for name `" + name
                    + "` have been successfully stopped");

            return true;
        } else {
            if (settings.isLog()) Tracer.info("There are no registered Netty Channels for name `" + name
                    + "` to be stopped");

            return false;
        }
    }

    public boolean closeAll() throws Exception {
        if (settings.isLog()) Tracer.info("Closing all Netty Channels");

        if (channels.isEmpty()) {
            if (settings.isLog()) Tracer.info("There are no registered Netty Channels");
            return false;
        }

        for (val channelName : channels.keys()) close(channelName);
        if (settings.isLog()) Tracer.info("All Netty Channels have been closed");

        return true;
    }

    public void startStandard(final String name, final ServerBootstrap bootstrap, final int port) throws Exception {
        open(name, bootstrap.channel(transportType.getServerSocketChannelClass())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true), port);
    }

    public void startHttp(final String name, final ServerBootstrap bootstrap, final int port) throws Exception {
        open(name, bootstrap.channel(transportType.getServerSocketChannelClass())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true), port);
    }

    public void shutdown() {
        childGroup.shutdownGracefully();
        parentGroup.shutdownGracefully();
    }
}
