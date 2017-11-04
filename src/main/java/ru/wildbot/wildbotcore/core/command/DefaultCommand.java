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

package ru.wildbot.wildbotcore.core.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.apache.commons.lang3.math.NumberUtils;
import ru.wildbot.wildbotcore.WildBotCore;
import ru.wildbot.wildbotcore.api.command.Command;
import ru.wildbot.wildbotcore.console.logging.AnsiCodes;
import ru.wildbot.wildbotcore.console.logging.Tracer;
import ru.wildbot.wildbotcore.secure.googleauth.AuthId;

import java.util.ArrayList;
import java.util.Arrays;

@AllArgsConstructor
public enum DefaultCommand {
    STOP(Command.builder()
            .name("stop")
            .name("end")
            .locked(true)
            .description("Stops WildBot Core")
            .executor((command, name, arguments) -> {
        Tracer.info("Stopping WildBot due to stop-command being called");
        WildBotCore.getInstance().disable();
        return null;
    }).build()),

    RESTART(Command.builder()
            .name("restart")
            .locked(true)
            .description("Restarts WildBot Core")
            .executor((command, name, arguments) -> {
                Tracer.info("Restarting WildBot due to restart-command being called");

                val restartRunnable = WildBotCore.restarter().getRestartRunnable();
                Runtime.getRuntime().addShutdownHook(new Thread(restartRunnable));

                WildBotCore.getInstance().disable();
                return null;
            }).build()),

    INFO(Command.builder()
            .name("info")
            .executor((command, name, args) -> {
        WildBotCore.getInstance().logInfo();
        return null;
    }).build()),

    HELP(Command.builder()
            .name("help")
            .executor((command, name, args) -> {
        val helpMessage = new StringBuilder(AnsiCodes.BG_WHITE)
                .append(AnsiCodes.FG_BLACK)
                .append("Commands available:");

        val commands = new ArrayList<Command>(WildBotCore.commandManager().getCommands());
        for (int i = 0; i < commands.size(); i++) {
            val cmd = commands.get(i);
            // Marker
            helpMessage.append("• ")
                    // Names
                    .append(cmd.getNames().toString())
                    // Is locked
                    .append(cmd.isLocked() ? " 🔐 : " : " : ")
                    // Plugin name
                    .append(cmd.getPluginName() == null || cmd.getPluginName().isEmpty() ? "☆"
                            : cmd.getPluginName())
                    // Description
                    .append(cmd.getDescription() == null || cmd.getDescription().isEmpty() ? ""
                            : " (" + cmd.getDescription() + ")")
                    // Usage
                    .append(cmd.getUsage() == null || cmd.getUsage().isEmpty() ? ""
                            : ":\n" + cmd.getUsage())
                    // New line or Colors-Reset
                    .append(i < commands.size() - 1 ? "\n" : AnsiCodes.RESET);
        }
        Tracer.info(helpMessage.toString());
        return null;
    }).build()),

    UPTIME(Command.builder()
            .name("uptime")
            .executor((command, name, args) -> {
                WildBotCore.getInstance().logInfo();
                return null;
            }).build()),

    AUTH_KEY_NEW(Command.builder()
            .name("auth_key_new")
            .executor((command, name, args) -> {
        if (args.size() >= 2) {

            val id = new AuthId(args.get(0), args.get(1));

            Tracer.info("Using AuthID: " + id);

            val key = WildBotCore.goggleAuthManager().newKey(id);

            Tracer.info("Key generated: " + key);
        } else Tracer.info("Insert your Key `platform` and `name`");

        return null;
    }).build()),

    AUTH_KEY_CHECK(Command.builder()
            .name("auth_key_check")
            .pluginName("")
            .description("Checks secure Google-2F-Auth Key")
            .usage(".. <platform> <name> <key>")
            .executor((command, name, args) -> {
        if (args.size() >= 3) {
            if (NumberUtils.isCreatable(args.get(2))) {
                val id = new AuthId(args.get(0), args.get(1));
                Tracer.info("Using AuthID: " + id);

                Tracer.info("Key-check result: " + WildBotCore.goggleAuthManager().auth(id,
                        NumberUtils.createNumber(args.get(2)).intValue()).name());
            } else Tracer.info("Given key is not a valid number");
        } else Tracer.info("Insert your Key `platform`, `name` and the very key");
        return null;
    }).build()),

    VKCB_STOP(Command.builder()
            .name("vkcb_stop")
            .pluginName("")
            .locked(true)
            .executor((command, name, args) -> {
        Tracer.info("Stopping VK-Callbacks");
        try {
            WildBotCore.vkCallbackServerManager().disable();
        } catch (Exception e) {
            Tracer.error("An exception occurred while trying to stop VK-Callbacks", e);
        }
        return null;
    }).build());

    /*
    AUTH_KEY_QR(Command.builder()
            .name("auth_key_qr")
            .pluginName("")
            .executor((command, name, arguments) -> {

                if (arguments.size() >= 2) {

                    val id = new AuthId(arguments.get(0), arguments.get(1));

                    Tracer.info("Using AuthID: " + id);

                    val qr = WildBotCore.goggleAuthManager().getQr(id);

                    Tracer.info("QR generated: " + key);
                } else Tracer.info("Insert your Key `platform` and `name`");

                return null;
            }).build());
    */

    @NonNull @Getter private final Command label;
}
