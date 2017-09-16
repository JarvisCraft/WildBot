package ru.wildcubes.wildbot.console.command;

import lombok.experimental.UtilityClass;
import org.slf4j.LoggerFactory;

@UtilityClass
public class CommandParser {
    public static synchronized void parseCommand(String command) {
        LoggerFactory.getLogger("CommandParser").info("Command executed!");
    }
}
