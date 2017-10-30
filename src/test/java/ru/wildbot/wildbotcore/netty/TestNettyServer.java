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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.junit.Test;
import ru.wildbot.wildbotcore.test.WildBotTest;

public class TestNettyServer extends WildBotTest {
    @Test
    public void testNettyStartupAndShutdown() throws Exception {
        testing("Netty Server Core Construction");
        final NettyServerCore nettyServerCore = new NettyServerCore();
        nettyServerCore.enable();
        success();

        testing("Netty Server Core Startup");
        nettyServerCore.open("test_netty_server", new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInboundHandlerAdapter()), 0);
        success();

        testing("Netty Server Core Shutdown");
        nettyServerCore.disable();
        success();

        allSuccess();
    }


    @Test
    public void testNettyNioMultiStartupAndShutdown() throws Exception {
        testing("Testing Netty Server Core Construction");
        final NettyServerCore nettyServerCore = new NettyServerCore();
        nettyServerCore.enable();
        success();

        testing("Netty Server Core Startup");
        nettyServerCore.open("test_netty_server1", new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInboundHandlerAdapter()), 0);
        nettyServerCore.open("test_netty_server2", new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInboundHandlerAdapter()), 0);
        success();

        testing("Netty Server Core Shutdown");
        nettyServerCore.disable();
        success();

        allSuccess();
    }

    @Test
    public void testNettyAutoHttpMultiStartupAndShutdown() throws Exception {
        testing("Netty Server Core Construction");
        final NettyServerCore nettyServerCore = new NettyServerCore();
        nettyServerCore.enable();
        success();

        testing("Netty Server Core Startup");
        nettyServerCore.startHttp("test_netty_server1", new ServerBootstrap()
                .childHandler(new ChannelInboundHandlerAdapter()), 0);
        nettyServerCore.startHttp("test_netty_server2", new ServerBootstrap()
                .childHandler(new ChannelInboundHandlerAdapter()), 0);
        success();

        testing("Testing Netty Server Core Shutdown");
        nettyServerCore.disable();
        success();

        allSuccess();
    }
}
