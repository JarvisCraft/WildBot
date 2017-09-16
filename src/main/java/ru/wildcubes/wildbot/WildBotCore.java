package ru.wildcubes.wildbot;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import lombok.Getter;
import ru.wildcubes.wildbot.api.plugin.PluginManager;
import ru.wildcubes.wildbot.console.command.CommandReader;
import ru.wildcubes.wildbot.console.logging.AnsiCodes;
import ru.wildcubes.wildbot.console.logging.Tracer;
import ru.wildcubes.wildbot.settings.SettingsManager;
import ru.wildcubes.wildbot.settings.SettingsReader;
import ru.wildcubes.wildbot.vk.VkApiManager;
import ru.wildcubes.wildbot.vk.server.VkCallbackServerManager;

import java.util.Scanner;

public class WildBotCore {
    @Getter public static final WildBotCore INSTANCE = new WildBotCore();

    private CommandReader commandReader;

    public static void main(String[] args) {
        Analytics.updateStartTime();

        Tracer.setupLogging();
        Tracer.outputLogo();

        PluginManager.loadPlugins();

        SettingsManager.init();
        SettingsReader.readRequiredSettings();

        Tracer.info(AnsiCodes.BG_GREEN + "It took " + Analytics.getUptimeFormatted() + " to start The Core"
                + AnsiCodes.RESET);
        Analytics.updateStartTime();

        VkApiManager.authorise();

        INSTANCE.commandReader = new CommandReader(){{
            start();
        }};

        try {
            VkCallbackServerManager.init();
        } catch (ApiException | ClientException e) {
            Tracer.error("An exception occurred while trying to enable HTTP-Callback server: ", e.getCause());
        }
    }
}
