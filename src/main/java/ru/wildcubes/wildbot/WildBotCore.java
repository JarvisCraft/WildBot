package ru.wildcubes.wildbot;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import ru.wildcubes.wildbot.logging.Tracer;
import ru.wildcubes.wildbot.settings.SettingsManager;
import ru.wildcubes.wildbot.settings.SettingsReader;
import ru.wildcubes.wildbot.vk.server.VkCallbackServerManager;
import ru.wildcubes.wildbot.vk.VkApiManager;

public class WildBotCore {
    public static void main(String[] args) {
        Tracer.setupLogging();
        Tracer.outputLogo();

        SettingsManager.init();
        SettingsReader.readRequiredSettings();

        VkApiManager.authorise();

        try {
            VkCallbackServerManager.init();
        } catch (ApiException | ClientException e) {
            Tracer.error("An exception occurred while trying to enable HTTP-Callback server:", e.getCause());
        }
    }
}
