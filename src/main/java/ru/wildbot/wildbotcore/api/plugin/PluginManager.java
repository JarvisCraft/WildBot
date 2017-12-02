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

package ru.wildbot.wildbotcore.api.plugin;

import lombok.Getter;
import lombok.val;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.io.FilenameUtils;
import ru.wildbot.wildbotcore.api.plugin.annotation.OnDisable;
import ru.wildbot.wildbotcore.api.plugin.annotation.OnEnable;
import ru.wildbot.wildbotcore.api.plugin.annotation.WildBotPluginData;
import ru.wildbot.wildbotcore.console.logging.Tracer;
import ru.wildbot.wildbotcore.util.FileHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginManager {
    private Set<JavaPluginInQueue> pluginsLoadQueue = new LinkedHashSet<>();

    @Getter
    private final CaseInsensitiveMap<String, WildBotAbstractPlugin> plugins = new CaseInsensitiveMap<>();

    public WildBotAbstractPlugin getPlugin(final String name) {
        return plugins.get(name);
    }

    public List<WildBotAbstractPlugin> getPlugins(final Class<? extends WildBotAbstractPlugin> pluginClass) {
        val pluginsList = new ArrayList<WildBotAbstractPlugin>();
        for (Map.Entry<String, WildBotAbstractPlugin> plugin : plugins.entrySet())
            if (pluginClass.isAssignableFrom(plugin.getValue().getClass())) pluginsList.add(plugin.getValue());
        return pluginsList;
    }

    public void enablePlugin(WildBotAbstractPlugin plugin) {
        enablePlugin(plugin, PluginHelper.getPluginData(plugin));
    }

    private void enablePlugin(WildBotAbstractPlugin plugin, WildBotPluginData pluginData) {
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
            val methods = plugin.getClass().getDeclaredMethods();
            for (Method method : methods)
                if (method.isAnnotationPresent(OnEnable.class)) {
                    val accessible = method.isAccessible();
                    method.setAccessible(true);
                    method.invoke(plugin);
                    method.setAccessible(accessible);
                }
        } catch (Exception e) {
            Tracer.error("An exception occurred while trying to call methods @OnEnable methods", e);
        }
        Tracer.info("Plugin \"" + pluginData.name() + "\" was successfully enabled");
    }

    public void disablePlugin(final String pluginName) {
        if (!plugins.containsKey(pluginName)) {
            Tracer.error("Unable to disable plugin by name \""
                    + pluginName + "\" as there's none enabled by it");
            return;
        }

        val plugin = plugins.get(pluginName);

        disablePlugin(plugin, PluginHelper.getPluginData(plugin));
    }

    public void disablePlugin(final WildBotAbstractPlugin plugin) {
        if (!plugins.containsValue(plugin)) {
            Tracer.error("Unable to disable plugin by class \"" + plugin.getClass().getSimpleName()
                    + "\" as there's none enabled by it");
            return;
        }

        disablePlugin(plugin, PluginHelper.getPluginData(plugin));
    }

    private void disablePlugin(final WildBotAbstractPlugin plugin, final WildBotPluginData pluginData) {
        Tracer.info("Disabling Plugin \"" + plugin.getClass().getSimpleName() + "\"");

        try {
            val methods = plugin.getClass().getMethods();
            for (Method method : methods) if (method.isAnnotationPresent(OnDisable.class)) method.invoke(plugin);
            plugins.remove(pluginData.name());
        } catch (Exception e) {
            Tracer.error("An exception occurred while trying to disable plugin by name \"" + pluginData.name()
                    + "\":", e);
        }

        Tracer.info("Plugin \"" + plugin.getClass().getSimpleName() + "\" was successfully disabled");
    }

    public static final String PLUGINS_FOLDER = "plugins";

    public void loadPlugins() {
        val files = loadJarFiles();
        for (File file : files) queueJarIfPlugin(file);

        sortPluginsQueue();

        for (JavaPluginInQueue plugin : pluginsLoadQueue) loadPlugin(plugin);
    }

    private void loadPlugin(final JavaPluginInQueue pluginInQueue) {
        Tracer.info("Loading plugin \"" + pluginInQueue.getJarName() + "\"");
        try {
            pluginInQueue.loadClasses();
            Tracer.info("Plugin \"" + pluginInQueue.getJarName() + "\" has been successfully loaded");

            Set<Class<? extends WildBotJavaPlugin>> pluginsClasses = pluginInQueue.getPluginsClasses();
            for (Class<? extends WildBotJavaPlugin> pluginsClass : pluginsClasses) {
                Tracer.info("Enabling plugin \"" + pluginInQueue.getJarName() + "\"");
                enablePlugin(pluginsClass.newInstance(), PluginHelper.getPluginData(pluginsClass));
                Tracer.info("Plugin \"" + pluginInQueue.getJarName() + "\" has been successfully enabled");
            }
        } catch (Exception e) {
            Tracer.error("An exception occurred while trying to enable plugin: ", e);
        }
    }

    private File[] loadJarFiles() {
        val folder = new File(PLUGINS_FOLDER);
        if (!folder.isDirectory() && !folder.exists() && folder.mkdirs()) Tracer
                .info("\"/plugins\" folder has been created");

        val filesArray = folder.listFiles(); // Holds all files in /plugins/ folder
        val jarFiles = new ArrayList<File>(); // Lists all `.jar` files

        if (filesArray == null) {
            Tracer.error("List of plugins happened to be null, aborting loading of plugins");
            return new File[0];
        }

        for (File file : filesArray) {
            if (file.isDirectory()) continue;
            if (FilenameUtils.isExtension(file.getName(), "jar")) {
                Tracer.info("Successfully found jar-file \"" + file.getName() + "\"");
                jarFiles.add(file);
            }
        }

        Tracer.info("Succesfully found " + jarFiles.size() + " .jar files in `/plugins/` folder: "
                + jarFiles.toString());

        return jarFiles.toArray(new File[jarFiles.size()]);
    }

    public static final String PLUGIN_MAIN_FILE_NAME = "main.wildbot";
    public static final String PLUGIN_DEPENDENCIES_FILE_NAME = "depend.wildbot";
    public static final String PLUGIN_SOFT_DEPENDENCIES_FILE_NAME = "softdepend.wildbot";
    public static final String PLUGIN_LOAD_BEFORE_DEPENDENCIES_FILE_NAME = "loadbefore.wildbot";

    private void queueJarIfPlugin(File file) {
        final JarFile jarFile;
        try {
            jarFile = new JarFile(file);
        } catch (IOException e) {
            Tracer.error("An exception occurred while trying to load plugin from .jar \""
                    + file.getName() + "\":", e);
            return;
        }

        JarEntry jarEntry;
        if ((jarEntry = jarFile.getJarEntry(PLUGIN_MAIN_FILE_NAME)) != null) {
            // "main.wildbot" loading
            val mainClasses = FileHelper.readLines(jarFile, jarEntry);
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
                        + " dependencies for Plugin \"" + file.getName() + "\": " + dependencies.toString());
            } else dependencies = new ArrayList<>();

            // "softdepend.wildbot" loading
            final List<String> softDependencies;
            if ((jarEntry = jarFile.getJarEntry(PLUGIN_SOFT_DEPENDENCIES_FILE_NAME)) != null) {
                softDependencies = FileHelper.readLines(jarFile, jarEntry);
                Tracer.info("Found " + softDependencies.size()
                        + " soft dependencies for Plugin \"" + file.getName() + "\": " + softDependencies.toString());
            } else softDependencies = new ArrayList<>();

            // "loadbefore.wildbot" loading
            final List<String> loadBefore;
            if ((jarEntry = jarFile.getJarEntry(PLUGIN_LOAD_BEFORE_DEPENDENCIES_FILE_NAME)) != null) {
                loadBefore = FileHelper.readLines(jarFile, jarEntry);
                Tracer.info("Found " + dependencies.size()
                        + " load-before's for Plugin \"" + file.getName() + "\": " + loadBefore.toString());
            } else loadBefore = new ArrayList<>();

            // Queue
            val pluginInQueue = new JavaPluginInQueue(file, jarFile, mainClasses,
                    dependencies, softDependencies, loadBefore);
            pluginsLoadQueue.add(pluginInQueue);
            Tracer.info("Plugin \"" + file.getName() + "\" was successfully added to queue");

        } else Tracer.warn("File \"/plugins/" + file.getName()
                + "\" does not contain main.wildbot, ignoring it");
    }

    /*private static void queueJarIfPlugin(File file) {

    }*/

    public void sortPluginsQueue() {
        Tracer.info("Sorting plugins' load-order");
        pluginsLoadQueue = new PluginQueueSorter(pluginsLoadQueue).sort();
        Tracer.info("Plugins' load-order has been successfully sorted");
    }
}
