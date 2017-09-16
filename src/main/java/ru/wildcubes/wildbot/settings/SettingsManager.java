package ru.wildcubes.wildbot.settings;

import ru.wildcubes.wildbot.console.logging.Tracer;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class SettingsManager {
    private static final String FILE_NAME = "settings.properties";

    public static void init() {
        Tracer.info("Loading SettingsManager");
        loadSettings();
        Tracer.info("SettingsManager has been successfully loaded");
    }

    private static final Properties DEFAULT_SETTINGS = new Properties() {{
        setProperty("server-start-test-tries", "8");
        setProperty("server-start-test-sleep", "1000");
        setProperty("language", "en_US");
    }};

    private static Properties settings;

    private static void loadSettings() throws RuntimeException {
        Tracer.info("Loading Settings");
        File file = new File(FILE_NAME);
        if (!file.exists() || file.isDirectory()) {
            Tracer.info("Could not find File \"settings.properties\", creating it now");
            file = createSettingsFile();
        }

        if (file == null) throw new RuntimeException("Error while loading \"settings.properties\" File");

        Properties settings = new Properties();
        try {
            InputStream inputStream = new FileInputStream(file);
            settings.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            Tracer.error("Error while trying to load Properties");
        }

        boolean isAddedNewProperty = false;
        for (Map.Entry<Object, Object> property : DEFAULT_SETTINGS.entrySet()) if (!settings
                .containsKey(property.getKey())) {
            settings.setProperty(String.valueOf(property.getKey()), String.valueOf(property.getValue()));
            isAddedNewProperty = true;
        }

        if (isAddedNewProperty) try {
            OutputStream outputStream = new FileOutputStream(file);
            settings.store(outputStream, "Main");
            outputStream.close();
        } catch (IOException e) { Tracer.error("Error while trying to save default Properties"); }

        SettingsManager.settings = settings;
        Tracer.info("Settings have been loaded successfully");
    }

    private static File createSettingsFile() {
        Tracer.info("Creating default File \"setting.properties\"");
        try {
            OutputStream outputStream = new FileOutputStream(new File(FILE_NAME));
            outputStream.close();
            return new File(FILE_NAME);
        } catch (IOException e) {
            Tracer.error("Error trying to create default \"settings.properties\" File");
            e.printStackTrace();
            return null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Settings Read/Write
    ///////////////////////////////////////////////////////////////////////////

    public static String getSetting(String key) {
        return settings.getProperty(key);
    }

    public static <T> T getSetting(String settingKey, Class<? extends T> settingClass) {
        final Object setting = settings.get(settingKey);
        try {
            return setting == null ? null : settingClass.cast(setting);
        } catch (ClassCastException e) {
            Tracer.warn("Unable to cast setting \"" + settingKey + "\" with value of \""
                    + setting +" \" to class \"" + settingClass.getSimpleName() + "\"");
            return null;
        }
    }

    public static void setSetting(String key, Object value, boolean save) {
        settings.setProperty(key, String.valueOf(value));

        if (save) saveSettings();
    }

    public static final String SETTINGS_COMMENT = "WildBot Main Configuration File.\n\n" +
            "WildBot is the product of JARvis PROgrammer (Russiа, Moscow) " +
            "made specially for WildCubes Minecraft Project.\n" +
            "This Program has nothing to do with Mojang AB, Microsoft or other companies related to Minecraft(TM)." +
            "It is a free open-source project authored by a young developer.\n\n" +
            "For contacting the developer use:\n" +
            "|_ mrjarviscraft@gmail.com\n" +
            "|_ https://vk.com/PROgrm_JARvis\n";

    public static void saveSettings() {
        try {
            final OutputStream outputStream = new FileOutputStream(new File(FILE_NAME));
            settings.store(outputStream, SETTINGS_COMMENT);
        } catch (IOException e) {
            Tracer.error("An error occurred while trying to save \"settings.properties\":", e.getCause());
        }
    }
}
