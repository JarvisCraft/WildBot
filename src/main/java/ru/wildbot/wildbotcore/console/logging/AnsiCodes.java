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

public final class AnsiCodes {
    public static final String
            RESET = "\033[0m",
            BOLD_ON = "\033[1m",
            ITALICS_ON = "\033[3m",
            UNDERLINE_ON = "\033[4m",
            INVERSE_ON = "\033[7m",
            STRIKETHROUGH_ON = "\033[9m",
            BOLD_OFF = "\033[22m",
            ITALICS_OFF = "\033[23m",
            UNDERLINE_OFF = "\033[24m",
            INVERSE_OFF = "\033[27m",
            STRIKETHROUGH_OFF = "\033[29m",
            FG_BLACK = "\033[30m",
            FG_RED = "\033[31m",
            FG_GREEN = "\033[32m",
            FG_YELLOW = "\033[33m",
            FG_BLUE = "\033[34m",
            FG_MAGENTA = "\033[35m",
            FG_CYAN = "\033[36m",
            FG_WHITE = "\033[37m",
            FG_DEFAULT = "\033[39m",
            BG_BLACK = "\033[40m",
            BG_RED = "\033[41m",
            BG_GREEN = "\033[42m",
            BG_YELLOW = "\033[43m",
            BG_BLUE = "\033[44m",
            BG_MAGENTA = "\033[45m",
            BG_CYAN = "\033[46m",
            BG_WHITE = "\033[47m",
            BG_DEFAULT = "\033[49m";
}
