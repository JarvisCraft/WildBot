package ru.wildcubes.wildbot.settings;

import lombok.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Builder
public class Setting {
    @Builder.Default @Getter @NonNull private String name = "unknown_field";
    @Builder.Default @Getter @NonNull private InputType inputType = InputType.STRING;
    @Builder.Default @Getter private int min = Integer.MIN_VALUE;
    @Builder.Default @Getter private int max = Integer.MAX_VALUE;

    @Getter @NonNull @Singular(value = "requestInputMessage") private List<String> requestInputMessage
            = Collections.singletonList("Please, input value for parameter {setting_name}");
    @Getter @NonNull @Singular(value = "wrongInputMessage") private List<String> wrongInputMessage
            = Collections.singletonList("Wrong value given for parameter {setting_name}");
    @Getter @NonNull @Singular(value = "successInputMessage") private List<String> successInputMessage
            = Collections.singletonList("Value of parameter {setting_name} set to {setting_value}");

    @AllArgsConstructor
    public enum InputType {
        BOOLEAN(boolean.class),
        SHORT(int.class),
        INT(int.class),
        LONG(long.class),
        DOUBLE(double.class),
        FLOAT(float.class),
        BIG_INT(BigDecimal.class),
        BIG_DEC(BigDecimal.class),
        SINGLE_STRING(String.class),
        STRING(String.class);

        @Getter private Class typeClass;
    }
}
