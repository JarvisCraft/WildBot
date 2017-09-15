package ru.wildcubes.wildbot.api.plugin;

public abstract class WildBotAbstractPlugin {
    protected void onEnable() {}
    protected void onDisable() {}

    protected abstract void enable();
    protected abstract void disable();
}
