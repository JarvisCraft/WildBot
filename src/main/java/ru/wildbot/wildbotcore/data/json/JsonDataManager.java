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

package ru.wildbot.wildbotcore.data.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.io.FileUtils;
import ru.wildbot.wildbotcore.console.logging.Tracer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * An advanced static manager
 */
@UtilityClass
public class JsonDataManager {
    @Getter private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @NonNull public <T extends AbstractJsonData> Optional<T> deserialize(final String json,
                                                                                Class<T> objectType) {
        return Optional.ofNullable(gson.fromJson(json, objectType));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Read
    ///////////////////////////////////////////////////////////////////////////

    @NonNull public <T extends AbstractJsonData> Optional<T> read(final File file, Class<T> objectType,
                                                                  final boolean create, final boolean write,
                                                                  final Tracer.LogType logType) {
        if (!file.exists() || file.isDirectory()) if (create) {
            try {
                Tracer.msg(logType, "Creating file for name \"" + file.getName() + "\"");

                @Cleanup val outputStream = FileUtils.openOutputStream(file);

                Tracer.msg(logType, "File for name \"" + file.getName() + "\" has been successfully created");

                if (write) {
                    Tracer.msg(logType, "Writing default contents to file \"" + file.getName() + "\"");

                    val data = objectType.newInstance();
                    outputStream.write(gson.toJson(data).getBytes(StandardCharsets.UTF_8));

                    Tracer.msg(logType, "Default contents have been successfully written to file \""
                            + file.getName() + "\"");

                    return Optional.ofNullable(data);
                } else return Optional.empty();
            } catch (IOException | IllegalAccessException | InstantiationException e) {
                Tracer.error("An exception occurred while trying to create file \""
                        + file.getName() + "\" or write to it:", e);

                return Optional.empty();
            }
        } else return Optional.empty();

        final String json;
        try {
            json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Optional.empty();
        }

        return deserialize(json, objectType);
    }

    @NonNull public <T extends AbstractJsonData> Optional<T> read(final File file, Class<T> objectType,
                                                                  final boolean create, final Tracer.LogType logType) {
        return read(file, objectType, create, true, logType);
    }

    @NonNull public <T extends AbstractJsonData> Optional<T> read(final File file, Class<T> objectType,
                                                                  final Tracer.LogType logType) {
        return read(file, objectType, true, true, logType);
    }

    @NonNull public <T extends AbstractJsonData> Optional<T> read(final String path, Class<T> objectType,
                                                                  final boolean create, final boolean write,
                                                                  final Tracer.LogType logType) {
        return read(new File(path), objectType, create, write, logType);
    }

    @NonNull public <T extends AbstractJsonData> Optional<T> read(final String path, Class<T> objectType,
                                                                  final boolean create,
                                                                  final Tracer.LogType logType) {
        return read(path, objectType, create, true, logType);
    }

    @NonNull public <T extends AbstractJsonData> Optional<T> read(final String path, Class<T> objectType,
                                                                  final Tracer.LogType logType) {
        return read(path, objectType, true, true, logType);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Write
    ///////////////////////////////////////////////////////////////////////////

    public <T> Optional<T> write(final File file, final T object, final Tracer.LogType logType) {
        try {
            FileUtils.write(file, gson.toJson(object), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Tracer.error("An exception occurred while trying to write file \""
                    + file.getName() + "\":", e);
        }
        return Optional.ofNullable(object);
    }

    public static <T> Optional<T> write(final String path, final T object, final Tracer.LogType logType) {
        return write(new File(path), object, logType);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Read and Write
    ///////////////////////////////////////////////////////////////////////////

    @NonNull public <T extends AbstractJsonData> Optional<T> readAndWrite(final File file, Class<T> objectType,
                                                                          final boolean create, final boolean write,
                                                                          final Tracer.LogType logType)
            throws JsonNotPresentException {
        return write(file, read(file, objectType, create, write, logType)
                .orElseThrow(JsonNotPresentException::new), logType);
    }

    @NonNull public <T extends AbstractJsonData> Optional<T> readAndWrite(final File file, Class<T> objectType,
                                                                          final boolean create,
                                                                          final Tracer.LogType logType)
            throws JsonNotPresentException {
        return readAndWrite(file, objectType, create, true, logType);
    }

    @NonNull public <T extends AbstractJsonData> Optional<T> readAndWrite(final File file, Class<T> objectType,
                                                                          final Tracer.LogType logType)
            throws JsonNotPresentException {
        return readAndWrite(file, objectType, true, true, logType);
    }

    @NonNull public <T extends AbstractJsonData> Optional<T> readAndWrite(final String path, Class<T> objectType,
                                                                          final boolean create, final boolean write,
                                                                          final Tracer.LogType logType)
            throws JsonNotPresentException {
        return readAndWrite(new File(path), objectType, create, write, logType);
    }

    @NonNull public <T extends AbstractJsonData> Optional<T> readAndWrite(final String path, Class<T> objectType,
                                                                          final boolean create,
                                                                          final Tracer.LogType logType)
            throws JsonNotPresentException {
        return readAndWrite(path, objectType, create, true, logType);
    }

    @NonNull public <T extends AbstractJsonData> Optional<T> readAndWrite(final String path, Class<T> objectType,
                                                                          final Tracer.LogType logType)
            throws JsonNotPresentException {
        return readAndWrite(path, objectType, true, true, logType);
    }
}
