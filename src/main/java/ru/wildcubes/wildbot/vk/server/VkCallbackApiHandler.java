package ru.wildcubes.wildbot.vk.server;

import com.vk.api.sdk.callback.CallbackApi;
import com.vk.api.sdk.objects.messages.Message;
import ru.wildcubes.wildbot.console.logging.Tracer;

public class VkCallbackApiHandler extends CallbackApi {
    @Override
    public void messageNew(Integer groupId, Message message) {
        super.messageNew(groupId, message);//TODO
        Tracer.info("mSg new from " + groupId);
    }
}
