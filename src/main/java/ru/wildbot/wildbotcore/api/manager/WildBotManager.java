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

package ru.wildbot.wildbotcore.api.manager;

import ru.wildbot.wildbotcore.api.exception.AlreadyDisabledException;
import ru.wildbot.wildbotcore.api.exception.AlreadyEnabledException;

public interface WildBotManager {
    void enable() throws Exception;
    void disable() throws Exception;

    boolean isEnabled();

    default boolean toggle() throws Exception {
        if (isEnabled()) disable();
        else enable();
        return isEnabled();
    }

    default void checkEnabled() throws Exception {
        if (isEnabled()) throw new AlreadyEnabledException("Manager already enabled");
    }

    default void checkDisabled() throws Exception {
        if (!isEnabled()) throw new AlreadyDisabledException("Manager not enabled");
    }
}
