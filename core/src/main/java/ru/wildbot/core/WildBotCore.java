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

package ru.wildbot.core;

import com.pengrad.telegrambot.TelegramBot;
import com.vk.api.sdk.client.VkApiClient;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.time.DurationFormatUtils;
import ru.wildbot.core.api.annotation.Shorthand;
import ru.wildbot.core.api.command.CommandManager;
import ru.wildbot.core.api.event.EventManager;
import ru.wildbot.core.api.plugin.PluginManager;
import ru.wildbot.core.api.provider.PaymentsProvider;
import ru.wildbot.core.console.logging.AnsiCodes;
import ru.wildbot.core.console.logging.Tracer;
import ru.wildbot.core.core.command.DefaultCommand;
import ru.wildbot.core.data.json.JsonDataManager;
import ru.wildbot.core.data.json.JsonNotPresentException;
import ru.wildbot.core.data.properties.PropertiesDataManager;
import ru.wildbot.core.data.properties.PropertiesDataReader;
import ru.wildbot.core.event.WildBotEnableEvent;
import ru.wildbot.core.netty.NettyServerCore;
import ru.wildbot.core.netty.NettyServerCoreSettings;
import ru.wildbot.core.provider.ProviderManager;
import ru.wildbot.core.rcon.httprcon.server.HttpRconServerManager;
import ru.wildbot.core.rcon.httprcon.server.HttpRconServerManagerSettings;
import ru.wildbot.core.rcon.rcon.server.RconServerManager;
import ru.wildbot.core.rcon.rcon.server.RconServerManagerSettings;
import ru.wildbot.core.restart.Restarter;
import ru.wildbot.core.restart.RestarterSettings;
import ru.wildbot.core.schedule.Scheduler;
import ru.wildbot.core.secure.googleauth.GoggleAuthManager;
import ru.wildbot.core.telegram.TelegramBotManager;
import ru.wildbot.core.telegram.TelegramBotManagerSettings;
import ru.wildbot.core.telegram.webhook.TelegramWebhookManager;
import ru.wildbot.core.telegram.webhook.TelegramWebhookManagerSettings;
import ru.wildbot.core.vk.VkManager;
import ru.wildbot.core.vk.VkManagerSettings;
import ru.wildbot.core.vk.callback.server.VkCallbackServerManager;
import ru.wildbot.core.vk.callback.server.VkCallbackServerManagerSettings;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Scanner;

/**
 * Main and base class of WildBot Project.
 * It provides a "constant" instance accessible via {@link #instance}
 * which is initialised on WildBot initialisation right after all required Managers are set up.
 * It does also initialise static classes like {@link Tracer} and {@link Analytics} up.
 *
 * Non-static managers can be accessed per unique instance's or rather from static {@link #instance}
 * using special methods marked as {@link Shorthand}
 */
public class WildBotCore {
    @Getter @Setter private static boolean bootstrap;

    ///////////////////////////////////////////////////////////////////////////
    // Singleton
    ///////////////////////////////////////////////////////////////////////////

    // Singleton Instance
    @NonNull @Getter private static final WildBotCore instance = new WildBotCore();

    ///////////////////////////////////////////////////////////////////////////
    // Main method
    ///////////////////////////////////////////////////////////////////////////

    public static void main(final String... args) {
        //for (val arg : args) if (arg.equals("--wildbot-bootstrap")) bootstrap = true; TODO

        final Instant beginTime = Instant.now();

        // Tracer initialisation
        Tracer.setupLogging();
        Tracer.outputLogo();

        if (bootstrap) Tracer.info("Started from Bootstrap");

        // Settings Manager (static)
        PropertiesDataManager.init();
        PropertiesDataReader.readRequiredSettings();

        // Core Managers

        Tracer.info("Enabling EventManager");
        instance.eventManager = new EventManager();
        Tracer.info("EventManager has been successfully enabled");

        Tracer.info("Enabling PluginManager");
        instance.pluginManager = new PluginManager();
        Tracer.info("PluginManager has been successfully enabled");


        Tracer.info("Enabling ProviderManager");
        instance.providerManager = new ProviderManager() {{
            registerEmpty(PaymentsProvider.class);
        }};
        Tracer.info("ProviderManager has been successfully enabled");

        Tracer.info("Enabling GoogleAuthManager");
        instance.goggleAuthManager = new GoggleAuthManager();
        Tracer.info("GoogleAuthManager has been successfully enabled");

        Tracer.info("Enabling Scheduler and Restarter");
        instance.setupScheduler();
        instance.setupRestarter();
        Tracer.info("Scheduler and Restarter have been successfully enabled");

        new WildBotEnableEvent(WildBotEnableEvent.Phase.REQUIRED_MANAGERS).call();

        // Netty Server Core
        instance.initNetty();

        new WildBotEnableEvent(WildBotEnableEvent.Phase.NETTY).call();

        instance.enableMessengers();
        instance.enableRcon();
        instance.enableHttpRcon();

        new WildBotEnableEvent(WildBotEnableEvent.Phase.OPTIONAL_MANAGERS).call();

        Tracer.info("HI, I am mister Missix, Look at me!"); // УУУУ, Пасхалочкаааа!

        instance.loadPlugins();

        new WildBotEnableEvent(WildBotEnableEvent.Phase.PLUGINS).call();

        Tracer.info("All components have been loaded successfully!");

        new WildBotEnableEvent(WildBotEnableEvent.Phase.READY).call();


        Tracer.info("Enabling CommandManager");
        instance.commandManager = new CommandManager();
        Tracer.info("CommandManager has been successfully enabled");

        instance.registerDefaultCommands();

        Tracer.info(AnsiCodes.BG_GREEN + "It took "
                + DurationFormatUtils.formatDurationHMS(Duration.between(beginTime, Instant.now()).toMillis())
                + " to start The Core" + AnsiCodes.RESET, "Now reading console input");
        instance.readCommands(); // Used for reading commands and not exiting application

        shutdown();
    }

    ///////////////////////////////////////////////////////////////////////////
    // `Enability`
    ///////////////////////////////////////////////////////////////////////////

    @Getter private boolean enabled = true;

    public void disable() {
        enabled = false;
        // TODO: 20.10.2017 if required
    }

    ///////////////////////////////////////////////////////////////////////////
    // Core Managers
    ///////////////////////////////////////////////////////////////////////////

    // Basic managers
    @Getter private Analytics analytics = new Analytics();
    @Shorthand public static Analytics analytics() {
        return instance.analytics;
    }

    @Getter private Scheduler scheduler;

    @Getter private Restarter restarter;
    @Shorthand public static Restarter restarter() {
        return instance.restarter;
    }

    @Getter private PluginManager pluginManager;
    @Shorthand public static PluginManager pluginManager() {
        return instance.pluginManager;
    }
    @Getter private EventManager eventManager;
    @Shorthand public static EventManager eventManager() {
        return instance.eventManager;
    }
    @Getter private ProviderManager providerManager;
    @Shorthand public static ProviderManager providerManager() {
        return instance.providerManager;
    }
    @Getter private CommandManager commandManager;
    @Shorthand public static CommandManager commandManager() {
        return instance.commandManager;
    }
    @Getter private GoggleAuthManager goggleAuthManager;
    @Shorthand public static GoggleAuthManager goggleAuthManager() {
        return instance.goggleAuthManager;
    }

    // Netty
    @Getter private NettyServerCore nettyServerCore;
    @Shorthand public static NettyServerCore nettyServerCore() {
        return instance.nettyServerCore;
    }

    private void setupScheduler() {// TODO: 01.11.2017 Manager
        scheduler = new Scheduler(Integer.parseInt(PropertiesDataManager.getSetting("scheduler-pool-size")));
    }

    private void setupRestarter() {// TODO: 01.11.2017 Manager
        try {
            restarter = new Restarter(JsonDataManager
                    .readAndWrite("restart.json", RestarterSettings.class)
                    .orElseThrow(JsonNotPresentException::new));
        } catch (Exception e) {
            Tracer.error("An exception occurred while trying to initialise Netty-Server-Core:", e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Plugin Manager
    ///////////////////////////////////////////////////////////////////////////

    private void loadPlugins() {
        Tracer.info("Loading plugins...");
        pluginManager.loadPlugins();
        Tracer.info("All possible plugins have been loaded");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Netty Server Core
    ///////////////////////////////////////////////////////////////////////////

    private void initNetty() {
        Tracer.info("Initialising Netty-Server-Core");
        try {
            nettyServerCore = new NettyServerCore(JsonDataManager
                    .readAndWrite("settings/netty/core.json", NettyServerCoreSettings.class)
                    .orElseThrow(JsonNotPresentException::new));
            nettyServerCore.enable();

            Tracer.info("Netty-Server-Core has been successfully initialised");
        } catch (Exception e) {
            Tracer.error("An exception occurred while trying to initialise Netty-Server-Core:", e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Default Social Networks, Messengers and Other
    ///////////////////////////////////////////////////////////////////////////

    // Messengers
    // VK
    @Getter private VkManager vkManager;
    @Shorthand public static VkManager vkApiManager() {
        return instance.vkManager;
    }
    @Shorthand public static VkApiClient vkApi() {
        return instance.vkManager.getVkApi();
    }

    @Getter private VkCallbackServerManager vkCallbackServerManager;
    @Shorthand public static VkCallbackServerManager vkCallbackServerManager() {
        return instance.vkCallbackServerManager;
    }
    // Telegram
    @Getter private TelegramBotManager telegramBotManager;
    @Shorthand public static TelegramBotManager telegramBotManager() {
        return instance.telegramBotManager;
    }
    @Shorthand public static TelegramBot telegramBot() {
        return instance.telegramBotManager.getBot();
    }
    @Getter private TelegramWebhookManager telegramWebhookManager;
    @Shorthand public static TelegramWebhookManager telegramWebhookManager() {
        return instance.telegramWebhookManager;
    }

    // RCON
    @Getter private RconServerManager rconServerManager;
    @Shorthand public static RconServerManager rconServerManager() {
        return instance.rconServerManager;
    }
    // HTTP-RCON
    @Getter private HttpRconServerManager httpRconServerManager;
    @Shorthand public static HttpRconServerManager httpRconServerManager() {
        return instance.httpRconServerManager;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Initialisations
    ///////////////////////////////////////////////////////////////////////////

    private void enableMessengers() {
        Tracer.info("Enabling Messengers");
        // Output
        enableVk();
        enableTelegram();

        // Input
        enableVkCallback();
        enableTelegramWebhook();
        Tracer.info("All possible Messengers have been enabled");
    }

    private void enableVk() {
        if (Boolean.parseBoolean(PropertiesDataManager.getSetting("enable-vk"))) {
            Tracer.info("Enabling VK module");
            try {
                vkManager = new VkManager(JsonDataManager
                        .readAndWrite("settings/vk/bot.json", VkManagerSettings.class)
                        .orElseThrow(JsonNotPresentException::new));
                vkManager.enable();
                Tracer.info("VK module has been successfully enabled");
            } catch (Exception e) {
                Tracer.error("An exception occurred while trying to enable VK module:", e);
            }
        }
    }


    private void enableVkCallback() {
        if (vkManager != null && Boolean.parseBoolean(PropertiesDataManager.getSetting("enable-vk-callback"))) {
            Tracer.info("Enabling VK Callbacks");
            try {
                vkCallbackServerManager = new VkCallbackServerManager(vkManager, JsonDataManager
                        .readAndWrite("settings/vk/callback.json", VkCallbackServerManagerSettings.class)
                        .orElseThrow(JsonNotPresentException::new));
                vkCallbackServerManager.enable();
                Tracer.info("VK Callbacks have been successfully enabled");
            } catch (Exception e) {
                Tracer.error("An exception occurred while trying to enable HTTP-Callback netty: ", e);
            }
        }
    }

    private void enableTelegram() {
        if (Boolean.parseBoolean(PropertiesDataManager.getSetting("enable-telegram"))) {
            Tracer.info("Enabling Telegram module");
            try {
                telegramBotManager = new TelegramBotManager(JsonDataManager
                        .readAndWrite("settings/telegram/bot.json", TelegramBotManagerSettings.class)
                        .orElseThrow(JsonNotPresentException::new));
                telegramBotManager.enable();
                Tracer.info("Telegram module has been successfully initialised");
            } catch (Exception e) {
                Tracer.error("An exception occurred while trying to enable Telegram module:", e);
            }
        }
    }

    private void enableTelegramWebhook() {
        if (Boolean.parseBoolean(PropertiesDataManager.getSetting("enable-telegram-webhook"))) {
            Tracer.info("Enabling Telegram Webhook");
            try {
                telegramWebhookManager = new TelegramWebhookManager(telegramBotManager, JsonDataManager
                        .readAndWrite("settings/telegram/webhook.json", TelegramWebhookManagerSettings.class)
                        .orElseThrow(JsonNotPresentException::new));
                telegramWebhookManager.enable();
                Tracer.info("Telegram Webhook module has been successfully initialised");
            } catch (Exception e) {
                Tracer.error("An exception occurred while trying to enable Telegram Webhook:", e);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // RCON
    ///////////////////////////////////////////////////////////////////////////

    private void enableRcon() {
        if (Boolean.parseBoolean(PropertiesDataManager.getSetting("enable-rcon"))) {
            Tracer.info("Enabling RCON");
            try {

                rconServerManager = new RconServerManager(JsonDataManager
                        .readAndWrite("settings/rcon/rcon.json", RconServerManagerSettings.class)
                        .orElseThrow(JsonNotPresentException::new));
                rconServerManager.enable();
                Tracer.info("RCON has been successfully enabled");
            } catch (Exception e) {
                Tracer.error("An exception occurred while trying to enable RCON: ", e);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // HTTP-RCON
    ///////////////////////////////////////////////////////////////////////////

    private void enableHttpRcon() {
        if (Boolean.parseBoolean(PropertiesDataManager.getSetting("enable-httprcon"))) {
            Tracer.info("Enabling HTTP-RCON");
            try {
                httpRconServerManager = new HttpRconServerManager(JsonDataManager
                        .readAndWrite("settings/rcon/httprcon.json", HttpRconServerManagerSettings.class)
                        .orElseThrow(JsonNotPresentException::new));
                httpRconServerManager.enable();
                Tracer.info("HTTP-RCON has been successfully enabled");
            } catch (Exception e) {
                Tracer.error("An exception occurred while trying to enable HTTP-RCON: ", e);
            }
        }
    }

    public void logInfo() {
        Tracer.info(Collections.singleton("Uptime: {}"), analytics.getUptimeFormatted());

        val plugins = pluginManager.getPlugins();

        Tracer.info(plugins.size() > 0 ? "There are no plugins loaded" : Collections
                        .singleton("Plugins loaded: {}{}"), plugins.size(), plugins.toString());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Commands
    ///////////////////////////////////////////////////////////////////////////

    private static Scanner scanner = new Scanner(System.in, "UTF-8");

    public void registerDefaultCommands() {
        for (val command : DefaultCommand.values()) commandManager.register(command.getLabel());
    }

    private void readCommands() {
        while (enabled && scanner.hasNextLine()) commandManager.parse(scanner.nextLine());
    }

    private static void shutdown() {
        Tracer.info("Shutting down");
        System.exit(0);
    }
}
