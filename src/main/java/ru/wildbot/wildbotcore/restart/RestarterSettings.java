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

package ru.wildbot.wildbotcore.restart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.wildbot.wildbotcore.data.json.AbstractJsonData;

import java.io.File;

@NoArgsConstructor
@AllArgsConstructor
public class RestarterSettings extends AbstractJsonData {
    /**
     * Commands to be used in {@link Runtime#exec(String[], String[], File)} as the first param.
     * Recommended value for windows is: {@code {"cmd /c start "[path-to-file]"}}
     * Recommended value for unix is: {@code {"/bin/sh", "-c", "[path-to-file]"}}
     */
    @Getter private String[] commands = {"/bin/sh", "-c", "start.sh"};

    /**
     * Command arguments to be used in {@link Runtime#exec(String[], String[], File)} as the second param.
     */
    @Getter private String[] arguments = {};

    /**
     * Path to file to be used in {@link Runtime#exec(String[], String[], File)} as the third param.
     * Uses {@link File#toPath()} on file created using this value.
     */
    @Getter private String path = "";
}
