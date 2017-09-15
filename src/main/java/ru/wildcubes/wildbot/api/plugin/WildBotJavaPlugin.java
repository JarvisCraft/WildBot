package ru.wildcubes.wildbot.api.plugin;

import ru.wildcubes.wildbot.logging.Tracer;

public abstract class WildBotJavaPlugin extends WildBotAbstractPlugin {
    @Override
    protected void onEnable() {
        Tracer.info("Disabling WildBot Plugin " + getClass().getSimpleName());
    }

    @Override
    protected void onDisable() {
        Tracer.info("Disabling WildBot Plugin " + getClass().getSimpleName());
    }

    @Override
    protected void enable() {
        PluginManager.enablePlugin(this);
    }

    @Override
    protected void disable() {
        PluginManager.disablePlugin(this);
    }
}
