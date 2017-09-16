package ru.wildcubes.wildbot.api.plugin;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PluginHelper {
    public static WildBotPluginData getPluginData(final WildBotAbstractPlugin plugin) {
        return plugin.getClass().getAnnotation(WildBotPluginData.class);
    }

    public static WildBotPluginData getPluginData(final Class<? extends WildBotAbstractPlugin> plugin) {
        return plugin.getAnnotation(WildBotPluginData.class);
    }
}
