package ru.wildcubes.wildbot.logging;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import lombok.Getter;
import ru.wildcubes.wildbot.WildBotCore;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tracer {
    @Getter public static final InternalLogger logger = Log4J2LoggerFactory.getInstance(WildBotCore.class);

    //General info
    public static void info(Collection<Object> objects) {
        if (objects != null && logger.isInfoEnabled()) for (Object object : objects) logger
                .info(String.valueOf(object));
    }
    public static void info(Object... objects) {
        if (objects != null && logger.isInfoEnabled()) for (Object object : objects) logger
                .info(String.valueOf(object));
    }
    //General info
    public static void infoF(Collection<Object> objects, Collection<Object> args) {
        if (objects != null && logger.isInfoEnabled()) for (Object object : objects) logger
                .info(String.valueOf(object), args);
    }
    //General info
    public static void infoP(Collection<Object> objects, Collection<Object> args) {
        if (objects != null && logger.isInfoEnabled()) {
            objects = formatWithPlaceholders(objects, args);
            for (Object object : objects) logger.info(String.valueOf(object), args);
        }
    }

    //Warnings
    public static void warn(Collection<Object> objects) {
        if (objects != null && logger.isWarnEnabled()) for (Object object : objects) logger
                .warn(String.valueOf(object));
    }
    public static void warn(Object... objects) {
        if (objects != null && logger.isWarnEnabled()) for (Object object : objects) logger
                .warn(String.valueOf(object));
    }
    //Warnings
    public static void warnF(Collection<Object> objects, Collection<Object> args) {
        if (objects != null && logger.isWarnEnabled()) for (Object object : objects) logger
                .warn(String.valueOf(object), args);
    }
    //Warnings
    public static void warnP(Collection<Object> objects, Collection<Object> args) {
        if (objects != null && logger.isWarnEnabled()) {
            objects = formatWithPlaceholders(objects, args);
            for (Object object : objects) logger.warn(String.valueOf(object), args);
        }
    }

    //Error
    public static void error(Collection<Object> objects) {
        if (objects != null && logger.isErrorEnabled()) for (Object object : objects) logger
                .error(String.valueOf(object));
    }
    public static void error(Object... objects) {
        if (objects != null && logger.isErrorEnabled()) for (Object object : objects) logger
                .error(String.valueOf(object));
    }
    //Error
    public static void errorF(Collection<Object> objects, Collection<Object> args) {
        if (objects != null && logger.isErrorEnabled()) for (Object object : objects) logger
                .error(String.valueOf(object), args);
    }
    //Error
    public static void errorP(Collection<Object> objects, Collection<Object> args) {
        if (objects != null && logger.isErrorEnabled()) {
            objects = formatWithPlaceholders(objects, args);
            for (Object object : objects) logger.error(String.valueOf(object), args);
        }
    }

    //General debug
    public static void debug(Collection<Object> objects) {
        if (objects != null && logger.isDebugEnabled()) for (Object object : objects) logger
                .debug(String.valueOf(object));
    }
    public static void debug(Object... objects) {
        if (objects != null && logger.isDebugEnabled()) for (Object object : objects) logger
                .debug(String.valueOf(object));
    }
    //General debug
    public static void debugF(Collection<Object> objects, Collection<Object> args) {
        if (objects != null && logger.isDebugEnabled()) for (Object object : objects) logger
                .debug(String.valueOf(object), args);
    }
    //General debug
    public static void debugP(Collection<Object> objects, Collection<Object> args) {
        if (objects != null && logger.isDebugEnabled()) {
            objects = formatWithPlaceholders(objects, args);
            for (Object object : objects) logger.debug(String.valueOf(object), args);
        }
    }

    //All important debug
    public static void trace(Collection<Object> objects) {
        if (objects != null && logger.isTraceEnabled()) for (Object object : objects) logger
                .debug(String.valueOf(object));
    }
    public static void trace(Object... objects) {
        if (objects != null && logger.isTraceEnabled()) for (Object object : objects) logger
                .debug(String.valueOf(object));
    }
    //All important debug
    public static void traceF(Collection<Object> objects, Collection<Object> args) {
        if (objects != null && logger.isTraceEnabled()) for (Object object : objects) logger
                .debug(String.valueOf(object), args);
    }
    //All important debug
    public static void traceP(Collection<Object> objects, Collection<Object> args) {
        if (objects != null && logger.isTraceEnabled()) {
            objects = formatWithPlaceholders(objects, args);
            for (Object object : objects) logger.debug(String.valueOf(object), args);
        }
    }

    public static Map<String, String> toPlaceholders(Object... args) {
        final Map<String, String> placeholders = new HashMap<>(args.length);
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

        for (int i = 0; i < objectList.size(); i++) {for (Map.Entry<String, String> placeholder
                : placeholders.entrySet()) objectList.set(i, String.valueOf(objectList.get(i))
                    .replaceAll(Pattern.quote(placeholder.getKey()), placeholder.getValue()));
        }

        return objects;
    }

    public static void outputLogo() {
        info(ASCII_LOGO);
    }

    private static final String LATEST_LOG = "logs/latest.log";

    public static void setupLogging() {
        final File file = new File(LATEST_LOG);
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(file));

            String previousLog = reader.readLine();

            if (previousLog != null) {
                reader.close();

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

                new PrintWriter(file).close();

                outputSessionInfo();
            } else {
                outputSessionInfo();
                info("No data found in \"latest.log\", adding it for you <3");
            }
        } catch (IOException e) {
            System.err.println("An error occurred while trying to update \".log\" files:");
            e.printStackTrace();
        }
    }

    public static void outputSessionInfo() {
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
