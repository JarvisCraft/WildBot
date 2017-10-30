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

package ru.wildbot.wildbotcore.data.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import ru.wildbot.wildbotcore.console.logging.Tracer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class PropertiesDataReader {
    private static final String SETTING_NAME_PLACEHOLDER = "{setting_name}";
    private static final String SETTING_VALUE_PLACEHOLDER = "{setting_value}";

    @Getter @Setter private static List<PropertiesDataRequired> requiredSettings = new ArrayList<>();

    public static void readRequiredSettings() {
        for (PropertiesDataRequired requiredSetting : requiredSettings) readSetting(requiredSetting);
    }

    public static void readSetting(final PropertiesDataRequired setting) {
        if (PropertiesDataManager.getSetting(setting.getName()) == null) {
            Tracer.infoP(Arrays.asList(setting.getRequestInputMessage().toArray()),
                    Arrays.asList(SETTING_NAME_PLACEHOLDER, setting.getName()));

            final Object settingValue = readApplicableSettingFromConsole(setting);

            PropertiesDataManager.setSetting(setting.getName(), settingValue, true);

            Tracer.infoP(Arrays.asList(setting.getSuccessInputMessage().toArray()),
                    Arrays.asList(SETTING_NAME_PLACEHOLDER, setting.getName(),
                            SETTING_VALUE_PLACEHOLDER, settingValue));
        }
    }


    private static Object readApplicableSettingFromConsole(final PropertiesDataRequired setting) {
        Object value = null;
        while (value == null) value = readSettingFromConsoleInput(setting);
        return value;
    }

    private static Object readSettingFromConsoleInput(final PropertiesDataRequired setting) {
        final Scanner scanner = new Scanner(System.in, "UTF-8");

        try {
            switch (setting.getInputType()) {
                case BOOLEAN: return Optional.of(scanner.nextBoolean());
                case SHORT: {
                    val value = scanner.nextShort();
                    if (value < setting.getMin()
                            || value > setting.getMax()) throw new InputMismatchException();
                    break;
                }
                case INT: {
                    val value = scanner.nextInt();
                    if (value < setting.getMin()
                            || value > setting.getMax()) throw new InputMismatchException();
                    return value;
                }
                case FLOAT: {
                    val value = scanner.nextFloat();
                    if (value < setting.getMin()
                            || value > setting.getMax()) throw new InputMismatchException();
                    return value;
                }
                case DOUBLE: {
                    val value = scanner.nextDouble();
                    if (value < setting.getMin()
                            || value > setting.getMax()) throw new InputMismatchException();
                }
                case BIG_INT: {
                    val value = scanner.nextBigInteger();
                    if (value.compareTo(BigInteger.valueOf(setting.getMin())) < 0
                            || value.compareTo(BigInteger.valueOf(setting.getMax())) > 0) throw
                            new InputMismatchException();
                    return value;
                }
                case BIG_DEC: {
                    val value = scanner.nextBigDecimal();
                    if (value.compareTo(BigDecimal.valueOf(setting.getMin())) < 0
                            || value.compareTo(BigDecimal.valueOf(setting.getMax())) > 0) throw
                            new InputMismatchException();
                    return value;
                }
                case LONG: {
                    val value = scanner.nextLong();
                    if (value < setting.getMin()
                            || value > setting.getMax()) throw new InputMismatchException();
                    return value;
                }
                case SINGLE_STRING: {
                    val value = scanner.next();
                    if (value.length() < setting.getMin()
                            || value.length() > setting.getMax()) throw new InputMismatchException();
                    return value;
                }
                case STRING: {
                    val value = scanner.nextLine();
                    if (value.length() < setting.getMin()
                            || value.length() > setting.getMax()) throw new InputMismatchException();
                    return value;
                }
                default: return scanner.nextLine();
            }
        } catch (InputMismatchException e) {
            Tracer.warnP(Arrays.asList(setting.getWrongInputMessage().toArray()),
                    SETTING_NAME_PLACEHOLDER, setting.getName());
        }
        return null;
    }
}
