package ru.wildcubes.wildbot.console.command;

import java.util.Scanner;

public class CommandReader extends Thread {
    private Scanner scanner = new Scanner(System.in);

    public CommandReader() {
        setDaemon(true);
    }

    @Override
    public void run() {
        while (scanner.hasNextLine()) {
            CommandParser.parseCommand(scanner.nextLine());
        }
    }
}
