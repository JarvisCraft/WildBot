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

package ru.wildbot.wildbotcore.rcon.rcon.server;

import io.netty.bootstrap.ServerBootstrap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.wildbot.wildbotcore.WildBotCore;
import ru.wildbot.wildbotcore.api.manager.WildBotManager;
import ru.wildbot.wildbotcore.api.manager.WildBotNettyManager;
import ru.wildbot.wildbotcore.console.logging.Tracer;
import ru.wildbot.wildbotcore.rcon.rcon.server.packet.RconPackets;

@RequiredArgsConstructor
public class RconServerManager implements WildBotManager, WildBotNettyManager {
    @Getter private boolean enabled = false;
    @Getter private boolean nettyEnabled = false;

    @NonNull @Getter private final RconServerManagerSettings settings;
    @NonNull private final RconPackets packets;

    @Override
    public void enable() throws Exception {
        checkEnabled();

        enableNetty();

        enabled = true;
    }

    @Override
    public void disable() throws Exception {
        checkDisabled();
        // TODO: 21.10.2017
        enabled = false;
    }

    public static final String NETTY_CHANNEL_NAME = "rcon";

    @Override
    public void enableNetty() throws Exception {
        checkNettyEnabled();

        Tracer.info("Starting RCON netty on port " + settings.getPort() + " by first: " + settings.getKey());

        WildBotCore.getInstance().getNettyServerCore().startStandard(NETTY_CHANNEL_NAME, new ServerBootstrap()
                .childHandler(new RconChannelInitializer(settings.getKey(), packets)), settings.getPort());

        Tracer.info("RCON netty has been successfully started");

        nettyEnabled = true;
    }

    @Override
    public void disableNetty() throws Exception {
        checkNettyDisabled();

        WildBotCore.nettyServerCore().close(NETTY_CHANNEL_NAME, settings.getPort());

        nettyEnabled = false;
    }
}
