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

import java.util.List;

/**
 * A self-descriptive functional interface (it's okay to create it using lambdas or method references if it's simple *)
 * which handles the given command according to it's input.
 * It's also worth mentioning that due to it's recursive nature
 * you can actually edit the very {@link Command} which is associated with this executor at the very time it's called.
 *
 * <p>(((* or if you are Dolya)))
 *
 * @see CommandManager
 */
@FunctionalInterface
public interface CommandExecutor {
    /**
     * The only required method of this {@link FunctionalInterface} which is being called.
     * @param command the command which is associated with this executor and which was called.
     * @param name the name by which the command was called (also may be called alias).
     * @param arguments the list of arguments which followed the command
     *                  (in {@link CommandManager} they are split by regex's `\s`
     * @return nullable runnable which may (but might not) be called after the command's execution
     */
    Runnable execute(Command command, String name, List<String> arguments);
}
