package ru.wildbot.core.vk.callback.event;

import com.vk.api.sdk.callback.objects.user.CallbackUserUnblock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.wildbot.core.api.event.WildBotEvent;

@AllArgsConstructor
public class VkUserUnblockEvent implements WildBotEvent {
    @Getter @Setter private Integer groupId;
    @Getter @Setter private CallbackUserUnblock userUnblock;
}
