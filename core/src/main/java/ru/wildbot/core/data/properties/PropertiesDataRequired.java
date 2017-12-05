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

package ru.wildbot.core.data.properties;

import lombok.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Builder
@EqualsAndHashCode
public class PropertiesDataRequired {
    @NonNull @Getter @Builder.Default private String name = "unknown_field";
    @NonNull @Getter @Builder.Default private InputType inputType = InputType.STRING;
    @Getter @Builder.Default private int min = Integer.MIN_VALUE;
    @Getter @Builder.Default private int max = Integer.MAX_VALUE;

    @NonNull @Getter @Singular(value = "requestInputMessage")
    private List<String> requestInputMessage
            = Collections.singletonList("Please, input second for parameter {setting_name}");
    @NonNull @Getter @Singular(value = "wrongInputMessage") private List<String> wrongInputMessage
            = Collections.singletonList("Wrong second given for parameter {setting_name}");
    @NonNull @Getter @Singular(value = "successInputMessage") private List<String> successInputMessage
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

        @Getter
        private Class typeClass;
    }
}
