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

package ru.wildbot.core.data.yaml;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;
import ru.wildbot.core.console.logging.Tracer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class YamlReader {
    // TODO
    @SuppressWarnings("unchecked")
    public static @NonNull Map<String, Object> read(final File file) {
        try {
            @Cleanup FileInputStream inputStream = FileUtils.openInputStream(file);

            val yamlObject = new Yaml().load(inputStream);
            return (Map<String, Object>) yamlObject;
        } catch (IOException e) {
            Tracer.error("An exception occurred while trying to load a YAML file:", e);
            return new HashMap<>();
        }
    }
}
