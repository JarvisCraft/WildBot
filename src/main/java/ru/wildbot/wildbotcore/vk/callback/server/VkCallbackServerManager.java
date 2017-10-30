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

package ru.wildbot.wildbotcore.vk.callback.server;

import com.vk.api.sdk.actions.Groups;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.groups.CallbackServer;
import io.netty.bootstrap.ServerBootstrap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import ru.wildbot.wildbotcore.WildBotCore;
import ru.wildbot.wildbotcore.api.manager.WildBotManager;
import ru.wildbot.wildbotcore.api.manager.WildBotNettyManager;
import ru.wildbot.wildbotcore.console.logging.Tracer;
import ru.wildbot.wildbotcore.vk.VkManager;

@RequiredArgsConstructor
public class VkCallbackServerManager implements WildBotManager, WildBotNettyManager {
    @Getter private boolean enabled = false;
    @Getter private boolean nettyEnabled = false;

    @NonNull private final VkManager vkManager;
    @NonNull private final VkCallbackServerManagerSettings settings;

    // VK API special
    private CallbackServer callbackServer = null;
    private String confirmationCode;
    @Getter private int id;

    @Override
    public void enable() throws Exception {
        checkEnabled();

        // Shorthands
        val group = vkManager.getVkApi().groups();
        val actor = vkManager.getActor();

        // Confirmation code (taken from VK-group)
        confirmationCode = group.getCallbackConfirmationCode(actor).execute().getCode();

        enableNetty();
        findCallbackServer(group, actor);
        registerCallbackServerIfAbsent(group, actor);

        Tracer.info("Using Host \"" + settings.getHost() + "\" for Callback Server");

        enabled = true;
    }

    @Override
    public void disable() throws Exception {
        checkDisabled();

        disableNetty();
        // TODO: 21.10.2017
        enabled = false;
    }

    public static final String NETTY_CHANNEL_NAME = "vk_callback";

    @Override
    public void enableNetty() throws Exception {
        checkNettyEnabled();

        Tracer.info("Starting VK-Callback netty on port: " + settings.getPort());

        WildBotCore.getInstance().getNettyServerCore().startHttp(NETTY_CHANNEL_NAME, new ServerBootstrap()
                .childHandler(new VkCallbackChannelInitializer(vkManager, confirmationCode)), settings.getPort());

        Tracer.info("VK-Callback netty has been successfully started");

        nettyEnabled = true;
    }

    @Override
    public void disableNetty() throws Exception {
        checkNettyDisabled();

        WildBotCore.nettyServerCore().close(NETTY_CHANNEL_NAME, settings.getPort());

        nettyEnabled = false;
    }

    public void findCallbackServer(Groups group, GroupActor actor) throws ApiException, ClientException {
        Tracer.info("Finding CallBack Server in the list of registered");

        val servers = group.getCallbackServers(actor).execute();

        for (CallbackServer callbackServerTested : servers.getItems())
            if (callbackServerTested.getUrl()
                    .equalsIgnoreCase(settings.getHost())) {
                Tracer.info("CallbackServer was found by host \"" + settings.getHost() + "\"");
                callbackServer = callbackServerTested;
                id = callbackServer.getId();
                break;
            }
    }

    public void registerCallbackServerIfAbsent(Groups group, GroupActor actor) throws ApiException, ClientException {
        Tracer.info("Registering custom Callback Server");

        if (callbackServer == null) {
            Tracer.info("There were no registered CallbackServer with host " + settings.getHost());
            id = group.addCallbackServer(actor, settings.getHost(), settings.getTitle()).execute().getServerId();
            findCallbackServer(group, actor);
        }
    }
}
