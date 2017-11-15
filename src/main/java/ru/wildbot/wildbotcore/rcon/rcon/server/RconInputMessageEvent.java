package ru.wildbot.wildbotcore.rcon.rcon.server;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.wildbot.wildbotcore.api.event.WildBotEvent;

import java.io.StringWriter;

@AllArgsConstructor
@Getter
public class RconInputMessageEvent implements WildBotEvent<RconInputMessageEvent> {

    private final Channel sender;
    private final StringWriter writer;
    private final String payload;
}
