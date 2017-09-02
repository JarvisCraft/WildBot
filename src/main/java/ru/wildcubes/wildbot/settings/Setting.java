package ru.wildcubes.wildbot.settings;

import java.math.BigDecimal;

public class Setting {
    private String name;
    private InputType inputType;

    private String[] requestInputMessage;
    private String[] wrongInputMessage;
    private String[] successInputMessage;
    
    public static final String[] REQUEST_INPUT_MESSAGE = {"Please, input value for parameter %1$s"};
    public static final String[] WRONG_INPUT_MESSAGE = {"Wrong value given for parameter %1$s"};
    public static final String[] SUCCESS_INPUT_MESSAGE = {"Value of parameter %1$s set to %2$s"};
    
    public Setting(String name, InputType inputType) {
        setName(name);
        setInputType(inputType);
    }

    public Setting(String name, InputType inputType, String[] requestInputMessage, String[] wrongInputMessage,
                   String[] successInputMessage) {
        setName(name);
        setInputType(inputType);
        setRequestInputMessage(requestInputMessage);
        setWrongInputMessage(wrongInputMessage);
        setSuccessInputMessage(successInputMessage);
    }

    public InputType getInputType() {
        return inputType;
    }

    public Setting setInputType(InputType inputType) {
        this.inputType = inputType;
        return this;
    }

    public String getName() {
        return name;
    }

    public Setting setName(String name) {
        this.name = name;
        return this;
    }

    public String[] getRequestInputMessage() {
        return requestInputMessage == null ? REQUEST_INPUT_MESSAGE : requestInputMessage;
    }

    public Setting setRequestInputMessage(String... requestInputMessage) {
        this.requestInputMessage = requestInputMessage;
        return this;
    }

    public String[] getWrongInputMessage() {
        return wrongInputMessage == null ? WRONG_INPUT_MESSAGE : wrongInputMessage;
    }

    public Setting setWrongInputMessage(String... wrongInputMessage) {
        this.wrongInputMessage = wrongInputMessage;
        return this;
    }

    public String[] getSuccessInputMessage() {
        return successInputMessage == null ? SUCCESS_INPUT_MESSAGE : successInputMessage;
    }

    public Setting setSuccessInputMessage(String... successInputMessage) {
        this.successInputMessage = successInputMessage;
        return this;
    }

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

        private Class typeClass;

        InputType(Class typeClass) {
            this.typeClass = typeClass;
        }

        public Class getTypeClass() {
            return typeClass;
        }
    }
}
