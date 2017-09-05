package ru.wildcubes.wildbot.settings;

import ru.wildcubes.wildbot.logging.Tracer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class SettingsReader {

    private static List<Setting> requiredSettings = new ArrayList<Setting>() {{
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
        add(new Setting("callback-server-host", Setting.InputType.SINGLE_STRING)
                .setRequestInputMessage("Please specify the host you would like to use for Callback Handling")
                .setWrongInputMessage("Given value is not a valid host")
                .setSuccessInputMessage("Callback Handling host set to TODO"));
        add(new Setting("callback-server-port", Setting.InputType.INT)
                .setRequestInputMessage("Please specify the port you would like to use for Callback Handling")
                .setWrongInputMessage("Given value is not a valid port")
                .setSuccessInputMessage("Callback Handling port set to TODO"));
        add(new Setting("callback-server-title", Setting.InputType.SINGLE_STRING, 1, 14)
                .setRequestInputMessage("Please specify the ID you would like to use for Callback Handling")
                .setWrongInputMessage("Given value is not a valid ID")
                .setSuccessInputMessage("Callback Handling ID set to TODO"));
        add(new Setting("callback-server-id", Setting.InputType.STRING, 0, 65536)
                .setRequestInputMessage("Please specify the ID you would like to use for Callback Handling")
                .setWrongInputMessage("Given value is not a valid ID")
                .setSuccessInputMessage("Callback Handling ID set to TODO"));
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
                case SHORT: {
                    short value = scanner.nextShort();
                    if (value < setting.getMin()
                            || value > setting.getMax()) throw new InputMismatchException();
                    return value;
                }
                case INT: {
                    int value = scanner.nextInt();
                    if (value < setting.getMin()
                            || value > setting.getMax()) throw new InputMismatchException();
                    return value;
                }
                case FLOAT: {
                    float value = scanner.nextFloat();
                    if (value < setting.getMin()
                            || value > setting.getMax()) throw new InputMismatchException();
                    return value;
                }
                case DOUBLE: {
                    double value = scanner.nextDouble();
                    if (value < setting.getMin()
                            || value > setting.getMax()) throw new InputMismatchException();
                }
                case BIG_INT: {
                    BigInteger value = scanner.nextBigInteger();
                    if (value.compareTo(BigInteger.valueOf(setting.getMin())) < 0
                            || value.compareTo(BigInteger.valueOf(setting.getMax())) > 0) throw
                            new InputMismatchException();
                    return value;
                }
                case BIG_DEC: {
                    BigDecimal value = scanner.nextBigDecimal();
                    if (value.compareTo(BigDecimal.valueOf(setting.getMin())) < 0
                            || value.compareTo(BigDecimal.valueOf(setting.getMax())) > 0) throw
                            new InputMismatchException();
                    return value;
                }
                case LONG: {
                    long value = scanner.nextLong();
                    if (value < setting.getMin()
                            || value > setting.getMax()) throw new InputMismatchException();
                    return value;
                }
                case SINGLE_STRING: {
                    String value = scanner.next();
                    if (value.length() < setting.getMin()
                            || value.length() > setting.getMax()) throw new InputMismatchException();
                    return value;
                }
                case STRING: {
                    String value = scanner.nextLine();
                    if (value.length() < setting.getMin()
                            || value.length() > setting.getMax()) throw new InputMismatchException();
                    return value;
                }
                default: return scanner.nextLine();
            }
        } catch (InputMismatchException e) {
            Tracer.warn((Object[]) setting.getWrongInputMessage());
            return readSettingFromConsole(setting);
        }
    }
}
