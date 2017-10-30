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

package ru.wildbot.wildbotcore.telegram.webhook;

import com.pengrad.telegrambot.request.GetWebhookInfo;
import com.pengrad.telegrambot.request.SetWebhook;
import io.netty.bootstrap.ServerBootstrap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.wildbot.wildbotcore.WildBotCore;
import ru.wildbot.wildbotcore.api.manager.WildBotManager;
import ru.wildbot.wildbotcore.api.manager.WildBotNettyManager;
import ru.wildbot.wildbotcore.console.logging.Tracer;
import ru.wildbot.wildbotcore.telegram.TelegramBotManager;

@RequiredArgsConstructor
public class TelegramWebhookManager implements WildBotManager, WildBotNettyManager {
    @Getter private boolean enabled = false;
    @Getter private boolean nettyEnabled = false;

    @NonNull @Getter private final TelegramBotManager botManager;
    @NonNull @Getter private final TelegramWebhookManagerSettings settings;

    @Override
    public void enable() throws Exception {
        checkEnabled();

        if (!botManager.execute(new GetWebhookInfo()).webhookInfo().url().equals(settings.getHost())) {
            Tracer.info("PropertiesDataRequired Telegram WebHook URL to: " + settings.getHost());

            botManager.execute(new SetWebhook().url(settings.getHost()).allowedUpdates(settings.getUpdates())
                    .maxConnections(settings.getMaxConnections()));

            Tracer.info("Telegram WebHook URL has been successfully is now set to: "
                    + botManager.execute(new GetWebhookInfo()).webhookInfo().url());
        }

        enableNetty();

        enabled = true;
    }

    @Override
    public void disable() throws Exception {
        checkDisabled();
        // TODO: 21.10.2017
        enabled = false;
    }

    public static final String NETTY_CHANNEL_NAME = "telegram_webhook";

    @Override
    public void enableNetty() throws Exception {
        checkNettyEnabled();

        Tracer.info("Starting Telegram-Webhook netty on port: " + settings.getPort());

        WildBotCore.getInstance().getNettyServerCore().startHttp(NETTY_CHANNEL_NAME, new ServerBootstrap()
                .childHandler(new TelegramWebhookChannelInitializer(botManager)), settings.getPort());

        Tracer.info("Telegram-Webhook netty has been successfully started");

        nettyEnabled = true;
    }

    @Override
    public void disableNetty() throws Exception {
        checkNettyDisabled();

        WildBotCore.nettyServerCore().close(NETTY_CHANNEL_NAME, settings.getPort());

        nettyEnabled = false;
    }
}
