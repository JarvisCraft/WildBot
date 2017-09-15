package ru.wildcubes.wildbot.api.plugin;

import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import ru.wildcubes.wildbot.api.plugin.annotation.WildBotPluginData;
import ru.wildcubes.wildbot.logging.AnsiCodes;
import ru.wildcubes.wildbot.logging.Tracer;
import ru.wildcubes.wildbot.util.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginManager {
    private static final Set<JavaPluginInQueue> pluginsLoadQueue = new LinkedHashSet<>();

    @Getter private static final Map<String, WildBotAbstractPlugin> plugins = new HashMap<>();

    public static void enablePlugin(WildBotAbstractPlugin plugin) {
        enablePlugin(plugin, plugin.getClass().getAnnotation(WildBotPluginData.class));
    }

    private static void enablePlugin(WildBotAbstractPlugin plugin, WildBotPluginData pluginData) {
        if (plugins.containsKey(pluginData.name())) {
            Tracer.error("Unable to enable plugin by name \"" + pluginData.name()
                    + "\" as there's already one registered by this name");
            return;
        }
        if (plugins.containsValue(plugin)) {
            Tracer.error("Unable to enable plugin by name \"" + pluginData.name()
                    + "\" as there's already one registered by this main class");
            return;
        }

        Tracer.info("Enabling Plugin \"" + pluginData.name() + "\"");
        try {
            plugin.onEnable();
        } catch (Exception e) {
            Tracer.error("An exception occurred while trying ");
        }
        Tracer.info("Plugin \"" + pluginData.name() + "\" was successfully enabled");
    }
/*

        else if (plugins.containsValue(plugin)) Tracer.error("Unable to disable plugin by name \""
                + pluginData.name() + "\" as there's none registered by this main class");
 */

    public static void disablePlugin(final String pluginName) {
        if (!plugins.containsKey(pluginName)) {
            Tracer.error("Unable to disable plugin by name \""
                    + pluginName + "\" as there's none enabled by it");
            return;
        }

        final WildBotAbstractPlugin plugin = plugins.get(pluginName);

        disablePlugin(plugin, plugin.getClass().getAnnotation(WildBotPluginData.class));
    }

    public static void disablePlugin(final WildBotAbstractPlugin plugin) {
        if (!plugins.containsValue(plugin)) {
            Tracer.error("Unable to disable plugin by class \"" + plugin.getClass().getSimpleName()
                    + "\" as there's none enabled by it");
            return;
        }

        disablePlugin(plugin, plugin.getClass().getAnnotation(WildBotPluginData.class));
    }

    private static void disablePlugin(final WildBotAbstractPlugin plugin, final WildBotPluginData pluginData) {
        Tracer.info("Disabling Plugin \"" + plugin.getClass().getSimpleName() + "\"");

        try {
            plugin.onDisable();
            plugins.remove(pluginData.name());
        } catch (Exception e) {
            Tracer.error("An exception occurred while trying to disable plugin by name \"" );
        }

        Tracer.info("Plugin \"" + plugin.getClass().getSimpleName() + "\" was successfully disabled");
    }

    public static final String PLUGINS_FOLDER = "plugins";

    public static void loadPlugins() {
        final File[] files = loadJarFiles();
        for (File file : files) {
            queueJarIfPlugin(file);
        }

        sortPluginsQueue();
    }

    private static File[] loadJarFiles() {
        final File folder = new File(PLUGINS_FOLDER);
        if (!folder.isDirectory() && !folder.exists()) if (folder.mkdirs()) Tracer
                .info("\"/plugins\" folder has been created");

        final List<File> files = new ArrayList<>();
        File[] filesArray = folder.listFiles();

        if (filesArray == null) {
            Tracer.error("List of plugins happened to be null, aborting loading of plugins");
            return (File[]) files.toArray();
        }

        Tracer.info("List of plugins happened to be null, aborting loading of plugins");

        files.addAll(Arrays.asList(filesArray));

        filesArray = new File[files.size()];

        int i = 0;
        for (File file : files) {
            if (file.isDirectory()) continue;
            if (FilenameUtils.isExtension(file.getName(), "jar")) {
                Tracer.info("Successfully found plugin \"" + file.getName() + "\"");
                filesArray[i] = file;
                i++;
            }
        }

        final StringBuilder filesNames = new StringBuilder(AnsiCodes.FG_YELLOW);

        for (i = 0; i < filesArray.length; i++) filesNames.append(filesArray[i].getName())
                .append((i < filesArray.length - 1) ? ", " : AnsiCodes.RESET);

        Tracer.info("Successfully found " + filesArray.length + " JarFiles in directory:", filesNames);

        return filesArray;
    }

    public static final String PLUGIN_MAIN_FILE_NAME = "main.wildbot";
    public static final String PLUGIN_DEPENDENCIES_FILE_NAME = "depend.wildbot";
    public static final String PLUGIN_SOFT_DEPENDENCIES_FILE_NAME = "softdepend.wildbot";

    private static void queueJarIfPlugin(File file) {
        final JarFile jarFile;
        try {
            jarFile = new JarFile(file);
        } catch (IOException e) {
            Tracer.error("An exception occurred while trying to load plugin from .jar \""
                    + file.getName() + "\":", e.getCause());
            return;
        }

        JarEntry jarEntry;
        if ((jarEntry = jarFile.getJarEntry(PLUGIN_MAIN_FILE_NAME)) != null) {
            // "main.wildbot" loading
            final List<String> mainClasses = FileHelper.readLines(jarFile, jarEntry);
            if (mainClasses.isEmpty()) {
                Tracer.warn("File \"/plugins/" + file.getName()
                        + "\"'s file main.wildbot is empty");
                return;
            } else
                Tracer.info("Found " + mainClasses.size()
                        + " main-classes for Plugin \"" + file.getName() + "\": " + mainClasses.toString());
            Tracer.info();

            // "depend.wildbot" loading
            final List<String> dependencies;
            if ((jarEntry = jarFile.getJarEntry(PLUGIN_DEPENDENCIES_FILE_NAME)) != null) {
                dependencies = FileHelper.readLines(jarFile, jarEntry);
                Tracer.info("Found " + dependencies.size()
                        + " dependencies for Plugin \"" + file.getName() + "\"");
            } else dependencies = new ArrayList<>();

            // "softdepend.wildbot" loading
            final List<String> softDependencies;
            if ((jarEntry = jarFile.getJarEntry(PLUGIN_SOFT_DEPENDENCIES_FILE_NAME)) != null) {
                softDependencies = FileHelper.readLines(jarFile, jarEntry);
                Tracer.info("Found " + dependencies.size()
                        + " dependencies for Plugin \"" + file.getName() + "\"");
            } else softDependencies = new ArrayList<>();

            // Queue
            final JavaPluginInQueue pluginInQueue = new JavaPluginInQueue(file, jarFile, mainClasses, dependencies,
                    softDependencies);
            pluginsLoadQueue.add(pluginInQueue);

        } else Tracer.warn("File \"/plugins/" + file.getName()
                + "\" does not contain main.wildbot, ignoring it");
    }

    /*private static void queueJarIfPlugin(File file) {

    }*/

    public static void sortPluginsQueue() {
        final Set<JavaPluginInQueue> queue = new LinkedHashSet<>();
        for (JavaPluginInQueue plugin : pluginsLoadQueue) {
            if (plugin.getDependencies().isEmpty() && plugin.getSoftDependencies().isEmpty()) {
                queue.add(plugin);
                infoPluginAddedToQueue(plugin);
                plugin.loadClasses();
            }
        }
    }
    private static void infoPluginAddedToQueue(JavaPluginInQueue pluginInQueue) {
        Tracer.info("Plugin \"" + pluginInQueue + "\" was added to the queue");
    }
}
