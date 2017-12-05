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

package ru.wildbot.core.api.manager;

import ru.wildbot.core.api.exception.AlreadyDisabledException;
import ru.wildbot.core.api.exception.AlreadyEnabledException;

public interface WildBotNettyManager {
    void enableNetty() throws Exception;
    void disableNetty() throws Exception;

    default boolean toggleNetty() throws Exception {
        if (isNettyEnabled()) disableNetty();
        else enableNetty();
        return isNettyEnabled();
    }

    boolean isNettyEnabled();

    default void checkNettyEnabled() throws Exception {
        if (isNettyEnabled()) throw new AlreadyEnabledException("Netty already enabled for Manager");
    }

    default void checkNettyDisabled() throws Exception {
        if (!isNettyEnabled()) throw new AlreadyDisabledException("Netty already disabled for Manager");
    }
}
