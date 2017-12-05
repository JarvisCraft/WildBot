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

package ru.wildbot.core.rcon.rcon.server;

import io.netty.channel.Channel;
import lombok.Data;
import ru.wildbot.core.api.event.WildBotEvent;

import java.io.StringWriter;

@Data
public class RconInputMessageEvent implements WildBotEvent<RconInputMessageEvent> {

    private final Channel sender;
    private final StringWriter writer;
    private final String payload;
}
