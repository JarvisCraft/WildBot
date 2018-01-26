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

package ru.wildbot.core.vk;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.GroupFull;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.wildbot.core.api.manager.WildBotManager;
import ru.wildbot.core.console.logging.Tracer;

@RequiredArgsConstructor
public class VkManager implements WildBotManager {
    @Getter private boolean enabled = false;

    ///////////////////////////////////////////////////////////////////////////
    // Secure
    ///////////////////////////////////////////////////////////////////////////

    @NonNull @Getter private final VkManagerSettings settings;

    @Getter private VkApiClient vkApi;

    @Getter @Setter private GroupActor actor;
    @Getter @Setter private GroupFull group;

    public static final String HELLO_WORLD = "kanya lapot";

    @Override
    public void enable() throws Exception {
        checkEnabled();

        vkApi = new VkApiClient(new HttpTransportClient());

        try {
            actor = new GroupActor(settings.getGroupId(), settings.getGroupKey());

            group = vkApi.groups().getById(actor).groupId(String.valueOf(settings.getGroupId())).execute().get(0);

            Tracer.info("Group \"" + group.getName()
                            + "\" has been successfully authorised by the following criteria:",
                    "ID: " + settings.getGroupId(), "Key: " + settings.getGroupKey());

            Tracer.info("Send: " + vkApi.messages().send(actor).userId(402833125).message(HELLO_WORLD)
                    .execute());
        } catch (ApiException | ClientException | IndexOutOfBoundsException e) {
            Tracer.error("Unable to enable VK.API, maybe wrong Group-ID / Group-Key was given:", e);
        }

        enabled = true;
    }

    @Override
    public void disable() throws Exception {
        checkDisabled();

        vkApi = null;

        actor = null;
        group = null;

        enabled = false;
    }
}