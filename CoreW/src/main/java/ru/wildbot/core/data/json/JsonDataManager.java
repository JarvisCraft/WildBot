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

package ru.wildbot.core.data.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.io.FileUtils;
import ru.wildbot.core.console.logging.Tracer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * An advanced {@link UtilityClass} used for working with various json-stored data parsed to
 * instances of classes extending {@link AbstractJsonData}.
 *
 * @see AbstractJsonData
 */
@UtilityClass
@SuppressWarnings("unused")
public class JsonDataManager {
    @Getter private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @NonNull public static <T extends AbstractJsonData> Optional<T> deserialize(final String json,
                                                                                Class<T> objectType) {
        return Optional.ofNullable(gson.fromJson(json, objectType));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Read
    ///////////////////////////////////////////////////////////////////////////

    @NonNull public static <T extends AbstractJsonData> Optional<T> read(final File file, Class<T> objectType,
                                                                         final boolean create, final boolean write) {
        if (!file.exists() || file.isDirectory()) if (create) {
            try {
                @Cleanup val outputStream = FileUtils.openOutputStream(file);

                if (write) {
                    val data = objectType.newInstance();
                    outputStream.write(gson.toJson(data).getBytes(StandardCharsets.UTF_8));

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

    @NonNull public static <T extends AbstractJsonData> Optional<T> read(final File file, Class<T> objectType,
                                                                         final boolean create) {
        return read(file, objectType, create, true);
    }

    @NonNull public static <T extends AbstractJsonData> Optional<T> read(final File file, Class<T> objectType) {
        return read(file, objectType, true, true);
    }

    @NonNull public static <T extends AbstractJsonData> Optional<T> read(final String path, Class<T> objectType,
                                                                         final boolean create, final boolean write) {
        return read(new File(path), objectType, create, write);
    }

    @NonNull public static <T extends AbstractJsonData> Optional<T> read(final String path, Class<T> objectType,
                                                                         final boolean create) {
        return read(path, objectType, create, true);
    }

    @NonNull public static <T extends AbstractJsonData> Optional<T> read(final String path, Class<T> objectType) {
        return read(path, objectType, true, true);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Write
    ///////////////////////////////////////////////////////////////////////////

    public static <T> Optional<T> write(final File file, final T object) {
        try {
            FileUtils.write(file, gson.toJson(object), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Tracer.error("An exception occurred while trying to write file \"" + file.getName() + "\":", e);
        }
        return Optional.ofNullable(object);
    }

    public static <T> Optional<T> write(final String path, final T object) {
        return write(new File(path), object);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Read and Write
    ///////////////////////////////////////////////////////////////////////////

    @NonNull public static <T extends AbstractJsonData> Optional<T> readAndWrite(final File file,
                                                                                 Class<T> objectType,
                                                                                 final boolean create,
                                                                                 final boolean write)
            throws JsonNotPresentException {
        return write(file, read(file, objectType, create, write).orElseThrow(JsonNotPresentException::new));
    }

    @NonNull public static <T extends AbstractJsonData> Optional<T> readAndWrite(final File file,
                                                                                 Class<T> objectType,
                                                                                 final boolean create)
            throws JsonNotPresentException {
        return readAndWrite(file, objectType, create, true);
    }

    @NonNull public static <T extends AbstractJsonData> Optional<T> readAndWrite(final File file,
                                                                                 Class<T> objectType)
            throws JsonNotPresentException {
        return readAndWrite(file, objectType, true, true);
    }

    @NonNull public static <T extends AbstractJsonData> Optional<T> readAndWrite(final String path,
                                                                                 Class<T> objectType,
                                                                                 final boolean create,
                                                                                 final boolean write)
            throws JsonNotPresentException {
        return readAndWrite(new File(path), objectType, create, write);
    }

    @NonNull public static <T extends AbstractJsonData> Optional<T> readAndWrite(final String path,
                                                                                 Class<T> objectType,
                                                                                 final boolean create)
            throws JsonNotPresentException {
        return readAndWrite(path, objectType, create, true);
    }

    @NonNull public static <T extends AbstractJsonData> Optional<T> readAndWrite(final String path,
                                                                                 Class<T> objectType)
            throws JsonNotPresentException {
        return readAndWrite(path, objectType, true, true);
    }
}
