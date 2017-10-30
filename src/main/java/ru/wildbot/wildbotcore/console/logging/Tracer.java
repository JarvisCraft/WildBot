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

import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2(topic = "WildBot")
public class Tracer {
    ///////////////////////////////////////////////////////////////////////////
    // Info
    ///////////////////////////////////////////////////////////////////////////

    public static void info(Collection<Object> objects) {
        if (objects != null && log.isInfoEnabled()) for (Object object : objects) log
                .info(String.valueOf(object));
    }

    public static void info(Object... objects) {
        if (objects != null && log.isInfoEnabled()) for (Object object : objects) log
                .info(String.valueOf(object));
    }

    public static void info(Collection<Object> objects, Object... args) {
        if (objects != null && log.isInfoEnabled()) for (Object object : objects) log
                .info(String.valueOf(object), args);
    }

    public static void infoF(Collection<Object> objects, Object... args) {
        if (objects != null && log.isInfoEnabled()) for (Object object : objects) log
                .info(String.format(String.valueOf(object), args));
    }

    public static void infoP(Collection<Object> objects, Object... args) {
        if (objects != null && log.isInfoEnabled()) {
            objects = formatWithPlaceholders(objects, args);
            for (Object object : objects) log.info(String.valueOf(object), args);
        }
    }

    public static void infoP(Collection<Object> objects, Collection<Object> args) {
        infoP(objects, args.toArray());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Warn
    ///////////////////////////////////////////////////////////////////////////

    public static void warn(Collection<Object> objects) {
        if (objects != null && log.isWarnEnabled()) for (Object object : objects) {
            if (object instanceof Throwable) object = ExceptionUtils.getStackTrace((Throwable) object);
            log.warn(AnsiCodes.FG_YELLOW + String.valueOf(object) + AnsiCodes.RESET);
        }
    }

    public static void warn(Object... objects) {
        if (objects != null && log.isWarnEnabled()) for (Object object : objects) {
            if (object instanceof Throwable) object = ExceptionUtils.getStackTrace((Throwable) object);
            log.warn(AnsiCodes.FG_YELLOW + String.valueOf(object) + AnsiCodes.RESET);
        }
    }

    public static void warn(Collection<Object> objects, Object... args) {
        if (objects != null && log.isWarnEnabled()) for (Object object : objects) {
            if (object instanceof Throwable) object = ExceptionUtils.getStackTrace((Throwable) object);
            log.warn(AnsiCodes.FG_YELLOW + String.valueOf(object) + AnsiCodes.RESET, args);
        }
    }

    public static void warnF(Collection<Object> objects, Object... args) {
        if (objects != null && log.isWarnEnabled()) for (Object object : objects) log
                .warn(String.format(String.valueOf(object), args));
    }

    public static void warnP(Collection<Object> objects, Object... args) {
        if (objects != null && log.isWarnEnabled()) {
            objects = formatWithPlaceholders(objects, args);
            for (Object object : objects) {
                if (object instanceof Throwable) object = ExceptionUtils.getStackTrace((Throwable) object);
                log.warn(AnsiCodes.FG_YELLOW + String.valueOf(object) + AnsiCodes.RESET, args);
            }
        }
    }

    public static void warnP(Collection<Object> objects, Collection<Object> args) {
        warnP(objects, args.toArray());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Error
    ///////////////////////////////////////////////////////////////////////////

    public static void error(Collection<Object> objects) {
        if (objects != null && log.isErrorEnabled()) for (Object object : objects) {
            if (object instanceof Throwable) object = ExceptionUtils.getStackTrace((Throwable) object);
            log.error(AnsiCodes.FG_RED + String.valueOf(object) + AnsiCodes.RESET);
        }
    }

    public static void error(Object... objects) {
        if (objects != null && log.isErrorEnabled()) for (Object object : objects) {
            if (object instanceof Throwable) object = ExceptionUtils.getStackTrace((Throwable) object);
            log.error(AnsiCodes.FG_RED + String.valueOf(object) + AnsiCodes.RESET);
        }
    }

    public static void error(Collection<Object> objects, Object... args) {
        if (objects != null && log.isErrorEnabled()) for (Object object : objects) {
            if (object instanceof Throwable) object = ExceptionUtils.getStackTrace((Throwable) object);
            log.error(AnsiCodes.FG_RED + String.valueOf(object) + AnsiCodes.RESET, args);
        }
    }

    public static void errorF(Collection<Object> objects, Object... args) {
        if (objects != null && log.isErrorEnabled()) for (Object object : objects) log
                .error(String.format(String.valueOf(object), args));
    }

    public static void errorP(Collection<Object> objects, Object... args) {
        if (objects != null && log.isErrorEnabled()) {
            objects = formatWithPlaceholders(objects, args);
            for (Object object : objects) {
                if (object instanceof Throwable) object = ExceptionUtils.getStackTrace((Throwable) object);
                log.error(AnsiCodes.FG_RED + String.valueOf(object) + AnsiCodes.RESET, args);
            }
        }
    }

    public static void errorP(Collection<Object> objects, Collection<Object> args) {
        errorP(objects, args.toArray());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Debug
    ///////////////////////////////////////////////////////////////////////////

    public static void debug(Collection<Object> objects) {
        if (objects != null && log.isDebugEnabled()) for (Object object : objects) log
                .debug(AnsiCodes.FG_CYAN + String.valueOf(object) + AnsiCodes.RESET);
    }

    public static void debug(Object... objects) {
        if (objects != null && log.isDebugEnabled()) for (Object object : objects) log
                .debug(AnsiCodes.FG_CYAN + String.valueOf(object) + AnsiCodes.RESET);
    }

    public static void debug(Collection<Object> objects, Object... args) {
        if (objects != null && log.isDebugEnabled()) for (Object object : objects) log
                .debug(AnsiCodes.FG_CYAN + String.valueOf(object) + AnsiCodes.RESET, args);
    }

    public static void debugF(Collection<Object> objects, Object... args) {
        if (objects != null && log.isDebugEnabled()) for (Object object : objects) log
                .debug(String.format(String.valueOf(object), args));
    }

    public static void debugP(Collection<Object> objects, Object... args) {
        if (objects != null && log.isDebugEnabled()) {
            objects = formatWithPlaceholders(objects, args);
            for (Object object : objects)
                log.debug(AnsiCodes.FG_CYAN + String.valueOf(object) + AnsiCodes.RESET, args);
        }
    }

    public static void debugP(Collection<Object> objects, Collection<Object> args) {
        debugP(objects, args.toArray());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Trace
    ///////////////////////////////////////////////////////////////////////////

    public static void trace(Collection<Object> objects) {
        if (objects != null && log.isTraceEnabled()) for (Object object : objects) log
                .debug(AnsiCodes.FG_BLUE + String.valueOf(object) + AnsiCodes.RESET);
    }

    public static void trace(Object... objects) {
        if (objects != null && log.isTraceEnabled()) for (Object object : objects) log
                .debug(AnsiCodes.FG_BLUE + String.valueOf(object) + AnsiCodes.RESET);
    }

    public static void trace(Collection<Object> objects, Object... args) {
        if (objects != null && log.isTraceEnabled()) for (Object object : objects) log
                .debug(AnsiCodes.FG_BLUE + String.valueOf(object) + AnsiCodes.RESET, args);
    }

    public static void traceF(Collection<Object> objects, Object... args) {
        if (objects != null && log.isTraceEnabled()) for (Object object : objects) log
                .trace(String.format(String.valueOf(object), args));
    }

    public static void traceP(Collection<Object> objects, Object... args) {
        if (objects != null && log.isTraceEnabled()) {
            objects = formatWithPlaceholders(objects, args);
            for (Object object : objects)
                log.debug(AnsiCodes.FG_BLUE + String.valueOf(object) + AnsiCodes.RESET, args);
        }
    }

    public static void traceP(Collection<Object> objects, Collection<Object> args) {
        traceP(objects, args.toArray());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Util
    ///////////////////////////////////////////////////////////////////////////

    public static Map<String, String> toPlaceholders(Object... args) {
        final Map<String, String> placeholders = new HashMap<>(args.length / 2);
        String latestKey = null;
        for (int i = 0; i < args.length; i++) {
            if (i % 2 == 0) latestKey = String.valueOf(args[i]);
            else placeholders.put(latestKey, String.valueOf(args[i]));
        }
        return placeholders;
    }


    public static Collection<Object> formatWithPlaceholders(final Collection<Object> objects, Object... args) {
        if (objects == null || objects.size() == 0) return null;

        final List<Object> objectList = new ArrayList<>(objects);
        final Map<String, String> placeholders = toPlaceholders(args);

        for (int i = 0; i < objectList.size(); i++) {
            for (Map.Entry<String, String> placeholder : placeholders.entrySet()) objectList
                    .set(i, String.valueOf(objectList.get(i))
                            .replace(placeholder.getKey(), placeholder.getValue()));

        }

        return objectList;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Setup
    ///////////////////////////////////////////////////////////////////////////

    public static void outputLogo() {
        info(ASCII_LOGO);
    }

    private static final String LATEST_LOG = "logs/latest.log";

    public static void setupLogging() {
        final File file = new File(LATEST_LOG);
        try {
            @Cleanup val reader = new BufferedReader(new InputStreamReader(FileUtils
                    .openInputStream(file), StandardCharsets.UTF_8));

            String previousLog = reader.readLine();

            if (previousLog != null) {
                final Matcher matcher = Pattern.compile("<(.*?)>").matcher(previousLog);
                if (matcher.find()) previousLog = matcher.group(1);
                else {
                    System.out.println("Could not get date for \"latest.log\". Using current time to save it");
                    previousLog = "unknown_"
                            + new SimpleDateFormat("YYYY-MM-dd_HH-mm-ss").format(new Date());
                }
                previousLog = "logs/" + previousLog + ".log";

                System.out.println("Saving previous log by name \"" + previousLog + "\"");

                Files.copy(file.toPath(), new File(previousLog).toPath());

                new PrintWriter(file, "UTF-8").close();

                outputSessionInfo();
            } else {
                outputSessionInfo();
                info("No yaml found in \"latest.log\", adding it for you <3");
            }
        } catch (IOException e) {
            System.err.println("An error occurred while trying to update \".log\" files:");
            e.printStackTrace();
        }
    }

    private static void outputSessionInfo() {
        info("<" + new SimpleDateFormat("YYYY-MM-dd_HH-mm-ss").format(new Date()) + ">");
    }

    private static final Object[] ASCII_LOGO = {
            "                                                                              ",
            "oooooo   oooooo     oooo  o8o  oooo        .o8  oooooooooo.                .  ",
            " `888.    `888.     .8'   `\"'  `888       \"888  `888'   `Y8b             .o8  ",
            "  `888.   .8888.   .8'   oooo   888   .oooo888   888     888  .ooooo.  .o888oo",
            "   `888  .8'`888. .8'    `888   888  d88' `888   888oooo888' d88' `88b   888  ",
            "    `888.8'  `888.8'      888   888  888   888   888    `88b 888   888   888  ",
            "     `888'    `888'       888   888  888   888   888    .88P 888   888   888 .",
            "      `8'      `8'       o888o o888o `Y8bod88P\" o888bood8P'  `Y8bod8P'   \"888\"",
            "                                                                              ",
            "                                                          by JARvis PROgrammer"
    };
}
