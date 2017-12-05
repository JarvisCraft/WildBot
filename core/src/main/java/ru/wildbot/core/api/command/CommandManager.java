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

package ru.wildbot.core.api.command;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import ru.wildbot.core.console.logging.Tracer;

import java.util.*;

public class CommandManager {
    @Getter private final Set<Command> commands = Sets.newConcurrentHashSet();

    @Getter @Setter private String unknownCommandMessage = "Unknown command";

    public boolean register(final Command command) {
        if (isRegistered(command.getNames())) {
            Tracer.warn("Unable to register command by names " + command.getNames()
                    + " as at least one of it's names is registered");
            return false;
        }
        commands.add(command);
        return true;
    }

    public boolean isRegistered(@NonNull final String name) {
        for (val command : commands) for (val commandName : command.getNames()) if (name
                .equalsIgnoreCase(commandName)) return true;
        return false;
    }

    public boolean isRegistered(@NonNull final Collection<String> names) {
        for (val command : commands) for (val commandName : command.getNames()) for (val name : names) if (name
                .equalsIgnoreCase(commandName)) return true;
        return false;
    }

    public Optional<Command> getCommand(@NonNull final String name) {
        for (val command : commands) for (val commandName : command.getNames()) if (name
                .equalsIgnoreCase(commandName)) return Optional.of(command);
        return Optional.empty();
    }

    public Optional<Command> getCommand(@NonNull final Collection<String> names) {
        for (val command : commands) for (val commandName : command.getNames()) for (val name : names) if (name
                .equalsIgnoreCase(commandName)) return Optional.of(command);
        return Optional.empty();
    }

    public Optional<Runnable> parse(String commandLine) {
        while (commandLine.indexOf(" ") == 0) commandLine = commandLine.substring(1); // remove spaces in the beginning
        final int firstSpaceIndex = commandLine.indexOf(' ');

        final String commandName = firstSpaceIndex >= 0 ? commandLine.substring(0, firstSpaceIndex) : commandLine;

        for (val command : commands) for (val testedCommandName : command.getNames()) if (commandName
                .equalsIgnoreCase(testedCommandName)) return Optional
                .ofNullable(command.getExecutor().execute(command, commandName, Arrays.asList(
                        firstSpaceIndex >= 0 ? commandLine.substring(firstSpaceIndex + 1).split("\\s")
                                : new String[]{""})));

        Tracer.info(unknownCommandMessage);
        return Optional.empty();
    }

    public Collection<Optional<Runnable>> parse(final String... commandLines) {
        val actions = new ArrayList<Optional<Runnable>>();

        for (val commandLine : commandLines) {
            if (commandLine == null || commandLine.isEmpty()) continue;
            actions.add(parse(commandLine));
        }

        return actions;
    }
}
