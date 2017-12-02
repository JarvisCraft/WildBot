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

package ru.wildbot.wildbotcore.test;

public class WildBotTest {
    private final static String SUCCESS = "Success";
    private final static String ALL_SUCCESS = "All tests successful";

    public void success() {
        System.out.println(SUCCESS);
    }

    public void allSuccess() {
        System.out.println(ALL_SUCCESS);
    }

    public void testing(final String tested) {
        System.out.printf("Testing %s%n", tested);
    }

    public static void info(final String message) {
        System.out.println(message);
    }

    public void err(final String message) {
        System.err.println(message);
    }

    public final WildBotTest assertException(final ThrowingRunnable test)
            throws UnsuccessfulException {
        try {
            test.run();
        } catch (Throwable e) {
            return this;
        }
        throw new UnsuccessfulException();
    }


    public final <T extends Class<? extends Throwable>> WildBotTest assertException(final ThrowingRunnable test,
                                                                                final T expected)
            throws UnsuccessfulException {
        try {
            test.run();
        } catch (Throwable e) {
            if (expected.isInstance(e)) return this;
        }
        throw new UnsuccessfulException(expected);
    }

    public static final class UnsuccessfulException extends Exception {
        public UnsuccessfulException() {
            super("No expected exception was thrown");
        }

        public UnsuccessfulException(final Class<? extends Throwable> exceptionClass) {
            super("Expected exception " + exceptionClass.getSimpleName() + " was not thrown");
        }
    }
}
