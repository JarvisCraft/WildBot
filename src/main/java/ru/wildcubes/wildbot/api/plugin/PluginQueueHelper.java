package ru.wildcubes.wildbot.api.plugin;

import lombok.experimental.UtilityClass;
import ru.wildcubes.wildbot.console.logging.Tracer;

import java.util.*;

@UtilityClass
public class PluginQueueHelper {
    public HashSet<JavaPluginInQueue> sortPluginsInQueue(final HashSet<JavaPluginInQueue> plugins) {
        // First all plugins are separated on groups and then added to `pluginsSorted` Set
        final LinkedHashSet<JavaPluginInQueue> pluginsOrdered = new LinkedHashSet<>();

        Tracer.info("Starting the process of sorting");
        infoPlugins(plugins, "unordered");
        infoPlugins(pluginsOrdered, "ordered");

        // Get all plugins without dependencies and remove them from `plugins` Set
        final LinkedHashSet<JavaPluginInQueue> independentPlugins = getIndependentPlugins(plugins);
        plugins.removeAll(independentPlugins);
        pluginsOrdered.addAll(independentPlugins);

        infoPlugins(pluginsOrdered, "independent ordered");
        infoPlugins(plugins, "unordered");
        infoPlugins(pluginsOrdered, "ordered");
        // First all plugins without dependencies get added

        // TODO: 16.09.2017

        plugins.clear();
        plugins.addAll(pluginsOrdered);
        
        return plugins;
    }

    private static String getPluginsListed(final Set<JavaPluginInQueue> plugins) {
        final List<JavaPluginInQueue> pluginsList = new ArrayList<>(plugins);

        final StringBuilder pluginsListed = new StringBuilder();
        for (int i = 0; i < pluginsList.size(); i++) pluginsListed.append(pluginsList.get(i).getJarName())
                .append(i < plugins.size() - 1 ? ", " : "");

        return pluginsListed.toString();
    }

    private static void infoPlugins(final Set<JavaPluginInQueue> plugins, String type) {
        Tracer.info("Plugins " + type + " (" + plugins.size() + "): " + getPluginsListed(plugins));
    }

    private static LinkedHashSet<JavaPluginInQueue> getIndependentPlugins(final Set<JavaPluginInQueue> plugins) {
        final LinkedHashSet<JavaPluginInQueue> independentPlugins = new LinkedHashSet<>();

        for (JavaPluginInQueue plugin : plugins) if (plugin.getDependencies().isEmpty()
                && plugin.getSoftDependencies().isEmpty()) independentPlugins.add(plugin);

        return independentPlugins;
    }

    private static LinkedHashMap<JavaPluginInQueue, Set<String>> getPluginsDependencies(
            final Set<JavaPluginInQueue> plugins) {
        final LinkedHashMap<JavaPluginInQueue, Set<String>> dependentPlugins = new LinkedHashMap<>();

        for (JavaPluginInQueue plugin : plugins) {
            final HashSet<String> dependencies = new HashSet<>();

            if (!plugin.getDependencies().isEmpty()) dependencies.addAll(plugin.getDependencies());
            if (!plugin.getSoftDependencies().isEmpty()) dependencies.addAll(plugin.getSoftDependencies());

            dependentPlugins.put(plugin, dependencies);
        }

        return dependentPlugins;
    }

    private static LinkedHashMap<JavaPluginInQueue, Set<String>> getPluginNames(final Set<JavaPluginInQueue> plugins) {
        final LinkedHashMap<JavaPluginInQueue, Set<String>> pluginNames = new LinkedHashMap<>();

        for (JavaPluginInQueue plugin : plugins) {
            Set<Class<? extends WildBotJavaPlugin>> pluginsClasses = plugin.getPluginsClasses();
            Set<String> names = new HashSet<>();

            for (Class<? extends WildBotJavaPlugin> pluginsClass : pluginsClasses) names
                    .add(PluginHelper.getPluginData(pluginsClass).name());

            pluginNames.put(plugin, names);
        }

        return pluginNames;
    }

    // TODO: 16.09.2017
    private static LinkedHashMap<JavaPluginInQueue, List<String>> getStartablePlugins(
            final Set<JavaPluginInQueue> plugins) {
        final LinkedHashMap<JavaPluginInQueue, List<String>> startablePlugins = new LinkedHashMap<>();

        for (JavaPluginInQueue plugin : plugins) if (!plugin.getDependencies().isEmpty()) startablePlugins.put(plugin,
                plugin.getDependencies());

        return startablePlugins;
    }
}
