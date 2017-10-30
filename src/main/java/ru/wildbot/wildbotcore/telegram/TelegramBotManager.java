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

package ru.wildbot.wildbotcore.telegram;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.wildbot.wildbotcore.api.manager.WildBotManager;

@RequiredArgsConstructor
public class TelegramBotManager implements WildBotManager {
    @Getter private boolean enabled = false;

    @NonNull private final TelegramBotManagerSettings settings;

    @Getter private TelegramBot bot;


    /**
     * Initialises Telegram Bot (for outcoming yaml)
     */
    @Override
    public void enable() throws Exception {
        checkEnabled();

        bot = TelegramBotAdapter.build(settings.getToken());

        enabled = true;
    }

    @Override
    public void disable() throws Exception {
        checkDisabled();
        // TODO: 21.10.2017
        enabled = false;
    }

    public <T extends BaseRequest, R extends BaseResponse> R execute(BaseRequest<T, R> request) {
        return bot.execute(request);
    }

    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(final T request, final Callback<T, R> callback) {
        bot.execute(request, callback);
    }
}
