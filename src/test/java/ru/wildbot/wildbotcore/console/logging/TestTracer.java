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

package ru.wildbot.wildbotcore.console.logging;

import org.junit.Test;
import ru.wildbot.wildbotcore.test.WildBotTest;

import java.util.Collections;

public class TestTracer extends WildBotTest {
    @Test
    public void testInfo() {
        Tracer.info("Tracer.info(String) Test successful");
        Tracer.info(this);
        Tracer.info(Collections.singleton("Tracer.info(Collection<Object>, Object...) {}"), "successful");
        Tracer.infoF(Collections.singleton("Tracer.infoF(Collection<Object>, Object...) %s"), "successful");
        Tracer.infoP(Collections.singleton("Test {first}"), "{first}", "successful");
    }

    @Override
    public String toString() {
        return "Tracer.info(Object) Test successful";
    }
}
