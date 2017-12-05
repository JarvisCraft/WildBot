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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Log4j2 based class for most console output.
 * While being {@link UtilityClass} this class is not available for extending so for your own custom debug
 * you should use Log4j2 (by default is stored in `latest.log`) or your own logging implementation (depends on you).
 * Also Lombok's annotation {@link Log4j2} is acceptable, moreover it's used right here.
 */
@Log4j2(topic = "WildBot")
@UtilityClass
@SuppressWarnings("unused")
public class Tracer {

    private final String LATEST_LOG = "logs/latest.log";

    private final Object[] ASCII_LOGO = {
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

    ///////////////////////////////////////////////////////////////////////////
    // Info
    ///////////////////////////////////////////////////////////////////////////

    public void info(final Object... objects) {
        if (objects != null && log.isInfoEnabled()) for (Object object : objects) log
                .info(String.valueOf(object));
    }

    public void info(final Collection<Object> objects) {
        if (objects == null) return;
        info(objects.toArray());
    }

    public void info(final Collection<Object> objects, final Object... args) {
        if (objects != null && log.isInfoEnabled()) for (Object object : objects) log
                .info(String.valueOf(object), args);
    }

    public void infoF(final Collection<Object> objects, final Object... args) {
        if (objects != null && log.isInfoEnabled()) for (Object object : objects) log
                .info(String.format(String.valueOf(object), args));
    }

    public void infoP(Collection<Object> objects, final Object... args) {
        if (objects != null && log.isInfoEnabled()) {
            objects = formatWithPlaceholders(objects, args);
            for (Object object : objects) log.info(String.valueOf(object), args);
        }
    }

    public void infoP(final Collection<Object> objects, final Collection<Object> args) {
        infoP(objects, args.toArray());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Warn
    ///////////////////////////////////////////////////////////////////////////

    public void warn(final Object... objects) {
        if (objects != null && log.isWarnEnabled()) for (Object object : objects) {
            if (object instanceof Throwable) object = ExceptionUtils.getStackTrace((Throwable) object);
            log.warn(AnsiCodes.FG_YELLOW + String.valueOf(object) + AnsiCodes.RESET);
        }
    }

    public void warn(final Collection<Object> objects) {
        if (objects == null) return;
        warn(objects.toArray());
    }

    public void warn(final Collection<Object> objects, final Object... args) {
        if (objects != null && log.isWarnEnabled()) for (Object object : objects) {
            if (object instanceof Throwable) object = ExceptionUtils.getStackTrace((Throwable) object);
            log.warn(AnsiCodes.FG_YELLOW + String.valueOf(object) + AnsiCodes.RESET, args);
        }
    }

    public void warnF(final Collection<Object> objects, final Object... args) {
        if (objects != null && log.isWarnEnabled()) for (Object object : objects) log
                .warn(String.format(String.valueOf(object), args));
    }

    public void warnP(Collection<Object> objects, final Object... args) {
        if (objects != null && log.isWarnEnabled()) {
            objects = formatWithPlaceholders(objects, args);
            for (Object object : objects) {
                if (object instanceof Throwable) object = ExceptionUtils.getStackTrace((Throwable) object);
                log.warn(AnsiCodes.FG_YELLOW + String.valueOf(object) + AnsiCodes.RESET, args);
            }
        }
    }

    public void warnP(final Collection<Object> objects, final Collection<Object> args) {
        warnP(objects, args.toArray());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Error
    ///////////////////////////////////////////////////////////////////////////

    public void error(final Object... objects) {
        if (objects != null && log.isErrorEnabled()) for (Object object : objects) {
            if (object instanceof Throwable) object = ExceptionUtils.getStackTrace((Throwable) object);
            log.error(AnsiCodes.FG_RED + String.valueOf(object) + AnsiCodes.RESET);
        }
    }

    public void error(final Collection<Object> objects) {
        if (objects == null) return;
        error(objects.toArray());
    }

    public void error(final Collection<Object> objects, final Object... args) {
        if (objects != null && log.isErrorEnabled()) for (Object object : objects) {
            if (object instanceof Throwable) object = ExceptionUtils.getStackTrace((Throwable) object);
            log.error(AnsiCodes.FG_RED + String.valueOf(object) + AnsiCodes.RESET, args);
        }
    }

    public void errorF(final Collection<Object> objects, final Object... args) {
        if (objects != null && log.isErrorEnabled()) for (Object object : objects) log
                .error(String.format(String.valueOf(object), args));
    }

    public void errorP(Collection<Object> objects, final Object... args) {
        if (objects != null && log.isErrorEnabled()) {
            objects = formatWithPlaceholders(objects, args);
            for (Object object : objects) {
                if (object instanceof Throwable) object = ExceptionUtils.getStackTrace((Throwable) object);
                log.error(AnsiCodes.FG_RED + String.valueOf(object) + AnsiCodes.RESET, args);
            }
        }
    }

    public void errorP(final Collection<Object> objects, final Collection<Object> args) {
        errorP(objects, args.toArray());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Debug
    ///////////////////////////////////////////////////////////////////////////

    public void debug(final Object... objects) {
        if (objects != null && log.isDebugEnabled()) for (Object object : objects) log
                .debug(AnsiCodes.FG_CYAN + String.valueOf(object) + AnsiCodes.RESET);
    }

    public void debug(final Collection<Object> objects) {
        if (objects == null) return;
        debug(objects.toArray());
    }

    public void debug(final Collection<Object> objects, final Object... args) {
        if (objects != null && log.isDebugEnabled()) for (Object object : objects) log
                .debug(AnsiCodes.FG_CYAN + String.valueOf(object) + AnsiCodes.RESET, args);
    }

    public void debugF(final Collection<Object> objects, final Object... args) {
        if (objects != null && log.isDebugEnabled()) for (Object object : objects) log
                .debug(String.format(String.valueOf(object), args));
    }

    public void debugP(Collection<Object> objects, final Object... args) {
        if (objects != null && log.isDebugEnabled()) {
            objects = formatWithPlaceholders(objects, args);
            for (Object object : objects)
                log.debug(AnsiCodes.FG_CYAN + String.valueOf(object) + AnsiCodes.RESET, args);
        }
    }

    public void debugP(final Collection<Object> objects, final Collection<Object> args) {
        debugP(objects, args.toArray());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Trace
    ///////////////////////////////////////////////////////////////////////////

    public void trace(final Object... objects) {
        if (objects != null && log.isTraceEnabled()) for (Object object : objects) log
                .debug(AnsiCodes.FG_BLUE + String.valueOf(object) + AnsiCodes.RESET);
    }

    public void trace(final Collection<Object> objects) {
        if (objects == null) return;
        trace(objects.toArray());
    }

    public void trace(final Collection<Object> objects, final Object... args) {
        if (objects != null && log.isTraceEnabled()) for (Object object : objects) log
                .debug(AnsiCodes.FG_BLUE + String.valueOf(object) + AnsiCodes.RESET, args);
    }

    public void traceF(final Collection<Object> objects, final Object... args) {
        if (objects != null && log.isTraceEnabled()) for (Object object : objects) log
                .trace(String.format(String.valueOf(object), args));
    }

    public void traceP(Collection<Object> objects, final Object... args) {
        if (objects != null && log.isTraceEnabled()) {
            objects = formatWithPlaceholders(objects, args);
            for (Object object : objects)
                log.debug(AnsiCodes.FG_BLUE + String.valueOf(object) + AnsiCodes.RESET, args);
        }
    }

    public void traceP(final Collection<Object> objects, final Collection<Object> args) {
        traceP(objects, args.toArray());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Util
    ///////////////////////////////////////////////////////////////////////////

    public Map<String, String> toPlaceholders(final Object... args) {
        final Map<String, String> placeholders = new HashMap<>(args.length / 2);
        String latestKey = null;
        for (int i = 0; i < args.length; i++) {
            if (i % 2 == 0) latestKey = String.valueOf(args[i]);
            else placeholders.put(latestKey, String.valueOf(args[i]));
        }
        return placeholders;
    }


    public final Collection<Object> formatWithPlaceholders(final Collection<Object> objects, final Object... args) {
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
    // Universal
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Enum containing all available Log4j logging types and all possible Trav{@link Consumer}s
     */
    @Getter
    @RequiredArgsConstructor
    public enum LogType {
        INFO(Tracer::info, Tracer::info, Tracer::info, Tracer::infoF, Tracer::infoP, Tracer::infoP),
        WARN(Tracer::warn, Tracer::warn, Tracer::warn, Tracer::warnF, Tracer::warnP, Tracer::warnP),
        ERROR(Tracer::error, Tracer::error, Tracer::error, Tracer::errorF, Tracer::errorP, Tracer::errorP),
        DEBUG(Tracer::debug, Tracer::debug, Tracer::debug, Tracer::debugF, Tracer::debugP, Tracer::debugP),
        TRACE(Tracer::trace, Tracer::trace, Tracer::trace, Tracer::traceF, Tracer::traceP, Tracer::traceP),
        NONE(null, null, null, null, null, null);

        /**
         * Objects-array message consumer.
         * @see Tracer#info(Object...)
         * @see Tracer#warn(Object...)
         * @see Tracer#error(Object...)
         * @see Tracer#debug(Object...)
         * @see Tracer#trace(Object...)
         */
        private final Consumer<Object[]> consumerMsgObjects;

        /**
         * Objects-collection message consumer.
         * @see Tracer#info(Collection)
         * @see Tracer#warn(Collection)
         * @see Tracer#error(Collection)
         * @see Tracer#debug(Collection)
         * @see Tracer#trace(Collection)
         */
        private final Consumer<Collection<Object>> consumerMsgCollection;

        /**
         * Message consumer for formatting objects-collection with objects-array argument using Log4j's formatting.
         * @see Tracer#info(Collection, Object...)
         * @see Tracer#warn(Collection, Object...)
         * @see Tracer#error(Collection, Object...)
         * @see Tracer#debug(Collection, Object...)
         * @see Tracer#trace(Collection, Object...)
         */
        private final BiConsumer<Collection<Object>, Object[]> consumerMsgCollectionObjects;

        /**
         * Message consumer for formatting objects-collection with objects-array argument using Java's formatting.
         * @see Tracer#infoF(Collection, Object...)
         * @see Tracer#warnF(Collection, Object...)
         * @see Tracer#errorF(Collection, Object...)
         * @see Tracer#debugF(Collection, Object...)
         * @see Tracer#traceF(Collection, Object...)
         */
        private final BiConsumer<Collection<Object>, Object[]> consumerMsgF;

        /**
         * Message consumer for formatting objects-collection with objects-array argument using placeholder-pairs.
         * @see Tracer#infoP(Collection, Object...)
         * @see Tracer#warnP(Collection, Object...)
         * @see Tracer#errorP(Collection, Object...)
         * @see Tracer#debugP(Collection, Object...)
         * @see Tracer#traceP(Collection, Object...)
         */
        private final BiConsumer<Collection<Object>, Object[]> consumerMsgPObjects;

        /**
         * Message consumer for formatting objects-collection with objects-collection argument using placeholder-pairs.
         * @see Tracer#infoP(Collection, Collection)
         * @see Tracer#warnP(Collection, Collection)
         * @see Tracer#errorP(Collection, Collection)
         * @see Tracer#debugP(Collection, Collection)
         * @see Tracer#traceP(Collection, Collection)
         */
        private final BiConsumer<Collection<Object>, Collection<Object>> consumerMsgPCollection;

        /**
         * Null-check {@link #consumerMsgObjects} and use it if possible by passing the arguments given.
         * @param objects - message-objects
         */
        public void msg(final Object... objects) {
            if (consumerMsgObjects == null) return;
            consumerMsgObjects.accept(objects);
        }

        /**
         * Null-check {@link #consumerMsgCollection} and use it if possible by passing the arguments given.
         * @param objects - message-objects
         */
        public void msg(final Collection<Object> objects) {
            if (consumerMsgCollection == null) return;
            consumerMsgCollection.accept(objects);
        }

        /**
         * Null-check {@link #consumerMsgCollectionObjects} and use it if possible by passing the arguments given.
         * @param objects - message-objects
         * @param args formatting values for Log4j
         */
        public void msg(final Collection<Object> objects, final Object... args) {
            if (consumerMsgCollectionObjects == null) return;
            consumerMsgCollectionObjects.accept(objects, args);
        }

        /**
         * Null-check {@link #consumerMsgF} and use it if possible by passing the arguments given.
         * @param objects - message-objects
         * @param args formatting values for {@link Formatter}
         */
        public void msgF(final Collection<Object> objects, final Object... args) {
            if (consumerMsgF == null) return;
            consumerMsgF.accept(objects, args);
        }

        /**
         * Null-check {@link #consumerMsgPObjects} and use it if possible by passing the arguments given.
         * @param objects - message-objects
         */
        public void msgP(Collection<Object> objects, final Object... args) {
            if (consumerMsgPObjects == null) return;
            consumerMsgPObjects.accept(objects, args);
        }

        /**
         * Null-check {@link #consumerMsgPCollection} and use it if possible by passing the arguments given.
         * @param objects - message-objects
         */
        public void msgP(final Collection<Object> objects, final Collection<Object> args) {
            if (consumerMsgPCollection == null) return;
            consumerMsgPCollection.accept(objects, args);
        }
    }

    public void msg(final LogType type, final Object... objects) {
        if (type == null) return;
        type.msg(objects);
    }

    public void msg(final LogType type, final Collection<Object> objects) {
        if (type == null) return;
        type.msg(objects);
    }

    public void msg(LogType type, final Collection<Object> objects, final Object... args) {
        if (type == null) return;
        type.msg(objects, args);
    }

    public void msgF(LogType type, final Collection<Object> objects, final Object... args) {
        if (type == null) return;
        type.msg(objects, args);
    }

    public void msgP(final LogType type, final Collection<Object> objects, final Object... args) {
        if (type == null) return;
        type.msg(objects, args);
    }

    public void msgP(final LogType type, final Collection<Object> objects, final Collection<Object> args) {
        if (type == null) return;
        type.msgP(objects, args);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Setup
    ///////////////////////////////////////////////////////////////////////////

    public void outputLogo() {
        info(ASCII_LOGO);
    }

    @Getter private boolean setUp = false;

    public void setupLogging() {
        if (setUp) return;

        val file = new File(LATEST_LOG);
        try {
            @Cleanup val reader = new BufferedReader(new InputStreamReader(FileUtils
                    .openInputStream(file), StandardCharsets.UTF_8));

            String previousLog = reader.readLine();

            if (previousLog != null) {
                val matcher = Pattern.compile("<(.*?)>").matcher(previousLog);
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
            } else {
                outputSessionInfo();
                info("No yaml found in \"latest.log\", adding it for you <3");
            }
        } catch (IOException e) {
            System.err.println("An error occurred while trying to update \".log\" files:");
            e.printStackTrace();
            return;
        }

        outputSessionInfo();

        setUp = true;
    }

    private void outputSessionInfo() {
        info("<" + new SimpleDateFormat("YYYY-MM-dd_HH-mm-ss").format(new Date()) + ">");
    }
}
