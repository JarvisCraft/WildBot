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

package ru.wildbot.core.telegram.webhook;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.wildbot.core.data.json.AbstractJsonData;

import java.util.Arrays;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TelegramWebhookManagerSettings extends AbstractJsonData {
    @NonNull private String host = "http://example.com/vk-webhook-netty";
    private int port = 12424;
    private int maxConnections = 40;
    @NonNull private String[] updates = {"*"};

    public String[] getUpdates() {
        return Arrays.copyOf(updates, updates.length);
    }
}
