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

package ru.wildbot.core.api.plugin;

import lombok.val;
import ru.wildbot.core.console.logging.Tracer;

import java.util.*;

public class PluginQueueSorter {
    private final Set<JavaPluginInQueue> pluginsQueue;

    public PluginQueueSorter(Set<JavaPluginInQueue> plugins) {
        if (plugins == null) pluginsQueue = new HashSet<>();
        else pluginsQueue = plugins;
    }

    public Set<JavaPluginInQueue> sort() {
        // Skip all actions if there are no plugins in queue
        if (pluginsQueue.isEmpty()) return pluginsQueue;

        infoPlugins(pluginsQueue, "to queue");

        val pluginsUnordered = new LinkedHashSet<JavaPluginInQueue>(pluginsQueue);

        // First all plugins are separated on groups and then added to `pluginsOrdered` Set
        val pluginsOrdered = new LinkedHashSet<JavaPluginInQueue>();

        sorting: {
            Tracer.info("Starting the process of sorting");
            infoPlugins(pluginsUnordered, "unordered");
            infoPlugins(pluginsOrdered, "ordered");

            // Remove all plugins which can not be sorted (having hard dependencies without dependencies present
            Tracer.info("Removing unloadable plugins from queue");
            pluginsUnordered.removeAll(getUnloadablePlugins());
            infoPlugins(pluginsUnordered, "unordered");
            infoPlugins(pluginsOrdered, "ordered");
            if (pluginsUnordered.isEmpty()) break sorting;

            // Get all plugins without dependencies and remove them from `unorderedPlugins` Set
            val independentPlugins = getIndependentPlugins(pluginsUnordered);
            pluginsUnordered.removeAll(independentPlugins);
            pluginsOrdered.addAll(independentPlugins);

            infoPlugins(pluginsOrdered, "independent ordered");
            infoPlugins(pluginsUnordered, "unordered");
            infoPlugins(pluginsOrdered, "ordered");
            // First all plugins without dependencies get added

            // TODO: 16.09.2017

            //plugins.clear();
            //plugins.addAll(pluginsOrdered);
        }

        return pluginsOrdered;
    }

    private Set<JavaPluginInQueue> getUnloadablePlugins() {
        val unloadablePlugins = new HashSet<JavaPluginInQueue>();
        val pluginNames = getPluginNames(pluginsQueue);

        pluginCheck:
        for (val plugin : pluginsQueue) {
            for (val dependency : plugin.getDependencies()) {
                if (!pluginNames.contains(dependency)) {
                    unloadablePlugins.add(plugin);
                    break pluginCheck;
                }
            }
        }

        return unloadablePlugins;
    }

    private LinkedHashSet<JavaPluginInQueue> getIndependentPlugins(final Set<JavaPluginInQueue> plugins) {
        val independentPlugins = new LinkedHashSet<JavaPluginInQueue>();

        for (val plugin : plugins) if (plugin.getDependencies().isEmpty()
                && plugin.getSoftDependencies().isEmpty()) independentPlugins.add(plugin);

        return independentPlugins;
    }

    private LinkedHashMap<JavaPluginInQueue, Set<String>> getPluginsDependencies(
            final Set<JavaPluginInQueue> plugins) {
        val dependentPlugins = new LinkedHashMap<JavaPluginInQueue, Set<String>>();

        for (JavaPluginInQueue plugin : plugins) {
            val dependencies = new HashSet<String>();

            if (!plugin.getDependencies().isEmpty()) dependencies.addAll(plugin.getDependencies());
            if (!plugin.getSoftDependencies().isEmpty()) dependencies.addAll(plugin.getSoftDependencies());

            dependentPlugins.put(plugin, dependencies);
        }

        return dependentPlugins;
    }

    private Set<String> getPluginNames(final Set<JavaPluginInQueue> plugins) {
            val pluginNames = new HashSet<String>();

        for (JavaPluginInQueue plugin : plugins) {
            val pluginsClasses = plugin.getPluginsClasses();
            for (val pluginsClass : pluginsClasses) pluginNames.add(PluginHelper.getPluginData(pluginsClass).name());
        }

        return pluginNames;
    }

    private LinkedHashMap<JavaPluginInQueue, Set<String>> getPluginNamesMap(final Set<JavaPluginInQueue> plugins) {
        val pluginNames = new LinkedHashMap<JavaPluginInQueue, Set<String>>();

        for (JavaPluginInQueue plugin : plugins) {
            val pluginsClasses = plugin.getPluginsClasses();
            val names = new HashSet<String>();

            for (val pluginsClass : pluginsClasses) names.add(PluginHelper.getPluginData(pluginsClass).name());

            pluginNames.put(plugin, names);
        }

        return pluginNames;
    }

    private LinkedHashMap<JavaPluginInQueue, List<String>> getStartablePlugins(
            final Set<JavaPluginInQueue> plugins) {
        val startablePlugins = new LinkedHashMap<JavaPluginInQueue, List<String>>();

        for (val plugin : plugins) if (!plugin.getDependencies().isEmpty()) startablePlugins
                .put(plugin, plugin.getDependencies());

        return startablePlugins;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Info
    ///////////////////////////////////////////////////////////////////////////

    private static String getPluginsListed(final Set<JavaPluginInQueue> plugins) {
        val pluginsList = new ArrayList<JavaPluginInQueue>(plugins);

        val pluginsListed = new StringBuilder();
        for (int i = 0; i < pluginsList.size(); i++)
            pluginsListed.append(pluginsList.get(i).getJarName())
                    .append(i < plugins.size() - 1 ? ", " : "");

        return pluginsListed.toString();
    }

    private static void infoPlugins(final Set<JavaPluginInQueue> plugins, String type) {
        if (plugins.size() >= 1) Tracer.info("Plugins " + type + " (" + plugins.size() + "): "
                + getPluginsListed(plugins));
        Tracer.info("No plugins " + type + " found");
    }
}
