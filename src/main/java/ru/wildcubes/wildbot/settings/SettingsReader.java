package ru.wildcubes.wildbot.settings;

import ru.wildcubes.wildbot.logging.Tracer;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class SettingsReader {

    public static List<Setting> requiredSettings = new ArrayList<Setting>() {{
        //VK API group access
        add(new Setting("group-id", Setting.InputType.INT)
                .setSuccessInputMessage("Enter your Group ID")
                .setWrongInputMessage("Not valid Group ID")
                .setWrongInputMessage("Group ID set to TODO")
        );
        add(new Setting("group-key", Setting.InputType.SINGLE_STRING)
                .setSuccessInputMessage("Enter your Group Access Key")
                .setWrongInputMessage("Invalid Group Access Key")
                .setWrongInputMessage("Group Access Key set to TODO")
        );

        // Callback Handling Server
        add(new Setting("server-host", Setting.InputType.SINGLE_STRING)
                .setRequestInputMessage("Please specify the host you would like to use for Callback Handling")
                .setWrongInputMessage("Given value is not a valid host")
                .setSuccessInputMessage("Callback Handling host set to TODO"));
        add(new Setting("server-port", Setting.InputType.INT)
                .setRequestInputMessage("Please specify the port you would like to use for Callback Handling")
                .setWrongInputMessage("Given value is not a valid port")
                .setSuccessInputMessage("Callback Handling port set to TODO"));
    }};

    public static void readRequiredSettings() {
        for (Setting requiredSetting : requiredSettings) readSetting(requiredSetting);
    }

    public static void readSetting(final Setting setting) {
        if (SettingsManager.getSetting(setting.getName()) == null) {
            Tracer.info((Object[]) setting.getRequestInputMessage());

            final Object settingValue = readSettingFromConsole(setting);

            SettingsManager.setSetting(setting.getName(), settingValue, true);

            Tracer.info((Object[]) setting.getSuccessInputMessage());
            Tracer.info(settingValue);
        }
    }

    private static Object readSettingFromConsole(final Setting setting) {
        final Scanner scanner = new Scanner(System.in);

        try {
            switch (setting.getInputType()) {
                case BOOLEAN: return scanner.nextBoolean();
                case SHORT: return scanner.nextShort();
                case INT: return scanner.nextInt();
                case FLOAT: return scanner.nextFloat();
                case DOUBLE: return scanner.nextDouble();
                case BIG_INT: return scanner.nextBigInteger();
                case BIG_DEC: return scanner.nextBigDecimal();
                case LONG: return scanner.nextLong();
                case SINGLE_STRING: return scanner.next();
                case STRING: return scanner.nextLine();
                default: return scanner.nextLine();
            }
        } catch (InputMismatchException e) {
            Tracer.warn((Object[]) setting.getWrongInputMessage());
            return readSettingFromConsole(setting);
        }
    }
}
