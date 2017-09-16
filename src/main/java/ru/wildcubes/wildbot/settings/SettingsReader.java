package ru.wildcubes.wildbot.settings;

import ru.wildcubes.wildbot.console.logging.Tracer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class SettingsReader {
    private static final String SETTING_NAME_PLACEHOLDER = "{setting_name}";
    private static final String SETTING_VALUE_PLACEHOLDER = "{setting_value}";

    private static List<Setting> requiredSettings = new ArrayList<Setting>() {{
        //VK API group access
        add(Setting.builder().name("group-id").inputType(Setting.InputType.INT)
                .requestInputMessage("Enter your Group ID")
                .wrongInputMessage("Not valid Group ID .")
                .successInputMessage("Group ID set to TODO")
                .build()
        );
        add(Setting.builder().name("group-key").inputType(Setting.InputType.SINGLE_STRING)
                .requestInputMessage("Enter your Group Access Key")
                .wrongInputMessage("Invalid Group Access Key")
                .successInputMessage("Group Access Key set to TODO")
                .build()
        );

        // Callback Handling Server
        add(Setting.builder().name("callback-server-host").inputType(Setting.InputType.SINGLE_STRING)
                .requestInputMessage("Please specify the host you would like to use for Callback Handling")
                .wrongInputMessage("Given value is not a valid host")
                .successInputMessage("Callback Handling host set to {setting_value}")
                .build()
        );
        add(Setting.builder().name("callback-server-port").inputType(Setting.InputType.INT)
                .requestInputMessage("Please specify the port you would like to use for Callback Handling")
                .wrongInputMessage("Given value is not a valid port")
                .successInputMessage("Callback Handling port set to {setting_value}")
                .build()
        );
    }};

    public static void readRequiredSettings() {
        for (Setting requiredSetting : requiredSettings) readSetting(requiredSetting);
    }

    public static void readSetting(final Setting setting) {
        if (SettingsManager.getSetting(setting.getName()) == null) {
            Tracer.infoP(Arrays.asList(setting.getRequestInputMessage().toArray()),
                    Arrays.asList(SETTING_NAME_PLACEHOLDER, setting.getName()));

            final Object settingValue = readSettingFromConsole(setting);

            SettingsManager.setSetting(setting.getName(), settingValue, true);

            Tracer.infoP(Arrays.asList(setting.getSuccessInputMessage().toArray()),
                    Arrays.asList(SETTING_NAME_PLACEHOLDER, setting.getName(),
                            SETTING_VALUE_PLACEHOLDER, settingValue));
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
            Tracer.warnP(Arrays.asList(setting.getWrongInputMessage().toArray()),
                    Arrays.asList(SETTING_NAME_PLACEHOLDER, setting.getName()));
            return readSettingFromConsole(setting);
        }
    }
}
