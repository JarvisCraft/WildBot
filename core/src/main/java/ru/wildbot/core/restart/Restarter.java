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

package ru.wildbot.core.restart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import ru.wildbot.core.console.logging.Tracer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@AllArgsConstructor
public class Restarter {
    @NonNull @Getter final RestarterSettings settings;

    public Runnable getRestartRunnable() {
        return () -> {
            try {
                Tracer.infoF(Collections.singleton("Restart settings:%nCommands: %s,%nArguments: %s,%nPath: %s"),
                        Arrays.toString(settings.getCommands()),
                        Arrays.toString(settings.getArguments()),
                        settings.getPath());
                Runtime.getRuntime().exec(settings.getCommands(),
                        settings.getArguments() == null || settings.getArguments().length == 0
                                ? null : settings.getArguments(),
                        settings.getPath() == null || settings.getPath().isEmpty()
                                ? null : new File(settings.getPath()));
                //Runtime.getRuntime().exec(new String[]{ "cmd.exe", "/c", "start.bat" });
            } catch (IOException e) {
                Tracer.error("An exception occurred while trying to restart:", e);
                e.printStackTrace();
            }
        };
    }
}
