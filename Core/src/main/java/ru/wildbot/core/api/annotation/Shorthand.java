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

package ru.wildbot.core.api.annotation;

import ru.wildbot.core.WildBotCore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate that given field or method is used as a shorthand for a longer variant.
 * As an example if there's always at least one instance of an object and each instance has getters and/or setters
 * for some field you can create a static Shorthand in the base class to get default instance's value of this field.
 * For example, {@link WildBotCore#eventManager()} is {@link Shorthand}
 * for {@code WildBotCore.getInstance().getEventManager()}
 *
 * @see WildBotCore
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Shorthand {}
