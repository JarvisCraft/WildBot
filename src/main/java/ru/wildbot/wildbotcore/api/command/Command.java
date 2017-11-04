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

@Builder
@EqualsAndHashCode
public class Command {
    @Singular @NonNull @Getter private Collection<String> names = new HashSet<>();
    @NonNull @Getter @Builder.Default private String pluginName = "";
    @Getter @Builder.Default private boolean locked = false;
    @NonNull @Getter private CommandExecutor executor;
    @NonNull @Getter @Builder.Default private String description = "";
    @Getter @Builder.Default private String usage = null;
}
