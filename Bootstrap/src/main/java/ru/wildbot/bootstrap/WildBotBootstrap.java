package ru.wildbot.bootstrap;

import ru.wildbot.wildbotcore.WildBotCore;

/**
 * The main class which plays the role of startpoint for WildBot
 */
public class WildBotBootstrap {
    public static void main(String[] args) {
        // TODO: 05.12.2017 Multi core
        new WildBotCore().main(args);
    }
}
