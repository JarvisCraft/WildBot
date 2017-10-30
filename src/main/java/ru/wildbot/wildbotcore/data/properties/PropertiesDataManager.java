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

package ru.wildbot.wildbotcore.data.properties;

import lombok.Cleanup;
import lombok.val;
import org.apache.commons.io.FileUtils;
import ru.wildbot.wildbotcore.console.logging.Tracer;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class PropertiesDataManager {
    private static final String FILE_NAME = "settings.properties";

    public static void init() {
        Tracer.info("Loading PropertiesDataManager");
        loadSettings();
        Tracer.info("PropertiesDataManager has been successfully loaded");
    }

    private static final Properties DEFAULT_SETTINGS = new Properties() {{
        // Locale
        setProperty("language", "en_US");
        // Netty
        setProperty("netty-boss-threads", "0");
        setProperty("netty-worker-threads", "0");
        // VK
        setProperty("enable-vk", "true");
        setProperty("enable-vk-callback", "true");
        setProperty("enable-telegram", "true");
        setProperty("enable-telegram-webhook", "false");
        // Http RCON
        setProperty("enable-rcon", "true");
        setProperty("enable-httprcon", "true");
    }};

    private static Properties settings;

    private static void loadSettings() throws RuntimeException {
        Tracer.info("Loading Settings");
        val file = new File(FILE_NAME);
        if (!file.exists() || file.isDirectory()) {
            Tracer.info("Could not find File \"yaml.properties\", creating it now");
            try {
                createSettingsFile(file);
            } catch (IOException e) {
                throw new RuntimeException("Error while loading \"yaml.properties\" File");
            }
        }

        Properties settings = new Properties();
        try {
            @Cleanup val inputStream = FileUtils.openInputStream(file);
            settings.load(inputStream);
        } catch (IOException e) {
            Tracer.error("Error while trying to load Properties");
        }

        boolean isAddedNewProperty = false;
        for (Map.Entry<Object, Object> property : DEFAULT_SETTINGS.entrySet())
            if (!settings.containsKey(property.getKey())) {
                settings.setProperty(String.valueOf(property.getKey()), String.valueOf(property.getValue()));
                isAddedNewProperty = true;
            }

        if (isAddedNewProperty) try {
            @Cleanup val outputStream = FileUtils.openOutputStream(file);
            settings.store(outputStream, "Main");
        } catch (IOException e) {
            Tracer.error("Error while trying to save default Properties");
        }

        PropertiesDataManager.settings = settings;
        Tracer.info("Settings have been loaded successfully");
    }

    private static void createSettingsFile(final File file) throws IOException {
        Tracer.info("Creating default File \"setting.properties\"");
        try {
            FileUtils.openOutputStream(file).close();
        } catch (IOException e) {
            Tracer.error("Error trying to create default \"yaml.properties\" File:", e);
            throw new IOException("File could not be created");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Settings Read/Write
    ///////////////////////////////////////////////////////////////////////////

    public static String getSetting(String key) {
        if (!settings.containsKey(key)) {
            settings.setProperty(key, "");
            saveSettings();
        }
        return settings.getProperty(key);
    }

    public static <T> T getSetting(String settingKey, Class<? extends T> settingClass) {
        final Object setting = settings.get(settingKey);
        try {
            return setting == null ? null : settingClass.cast(setting);
        } catch (ClassCastException e) {
            Tracer.warn("Unable to cast setting \"" + settingKey + "\" with second of \""
                    + setting + " \" to class \"" + settingClass.getSimpleName() + "\"");
            return null;
        }
    }

    public static void setSetting(String key, Object value, boolean save) {
        settings.setProperty(key, String.valueOf(value));

        if (save) saveSettings();
    }

    private static final String SETTINGS_COMMENT = "WildBot Main Configuration File.\n\n" +
            "WildBot is the product of JARvis PROgrammer (Russia, Moscow) " +
            "made specially for WildCubes Minecraft Project.\n" +
            "This Program has nothing to do with Mojang AB, Microsoft or other companies related to Minecraft(TM)." +
            "It is a free open-source project authored by a young developer.\n\n" +
            "For contacting the developer use:\n" +
            "|_ mrjarviscraft@gmail.com\n" +
            "|_ https://vk.com/PROgrm_JARvis\n";

    public static void saveSettings() {
        try {
            @Cleanup val outputStream = FileUtils.openOutputStream(new File(FILE_NAME));
            settings.store(outputStream, SETTINGS_COMMENT);
        } catch (IOException e) {
            Tracer.error("An error occurred while trying to save \"yaml.properties\":", e);
        }
    }
}
