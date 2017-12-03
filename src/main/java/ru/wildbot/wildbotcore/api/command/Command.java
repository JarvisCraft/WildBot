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

package ru.wildbot.wildbotcore.api.command;

import lombok.*;

import java.util.Collection;
import java.util.HashSet;

/**
 * A POJO representing a command used by {@link CommandManager}.
 * @see CommandManager
 */
@Builder
@EqualsAndHashCode
public class Command {
    /**
     * Names by which the command is being registered and accessed.
     */
    @Singular @NonNull @Getter private Collection<String> names;

    /**
     * Plugin name to be used for identifying this command.
     * Empty value ({@code ""}) means that there's no plugin associated with this command,
     * e.g. if this command is one of WildBot's default commands.
     */
    @NonNull @Getter @Builder.Default private String pluginName;

    /**
     * If set to true then this command cannot be overridden.
     * E.g. {@link CommandManager#register(Command)} will check this flag when being called.
     * TODO REregister
     */
    @Getter @Builder.Default private boolean locked;

    /**
     * An executor used by this command to perform the very action
     */
    @NonNull @Getter private CommandExecutor executor;

    /**
     * Optional description for this command.
     * E.g. used with {@link ru.wildbot.wildbotcore.core.command.DefaultCommand#HELP}
     */
    @NonNull @Getter @Builder.Default private String description;

    /**
     * Optional description for this command's usage.
     * E.g. used with {@link ru.wildbot.wildbotcore.core.command.DefaultCommand#HELP}
     */
    @Getter @Builder.Default private String usage;
}
