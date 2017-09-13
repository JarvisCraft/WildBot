package ru.wildcubes.wildbot.api.plugin;

import org.apache.commons.io.FilenameUtils;
import ru.wildcubes.wildbot.logging.AnsiCodes;
import ru.wildcubes.wildbot.logging.Tracer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginManager {
    private static final Set<JarFile> pluginsLoadQueue = new HashSet<>();

    private static final Map<String, WildBotAbstractPlugin> plugins = new HashMap<>();

    public static final String PLUGINS_FOLDER = "plugins";

    public static Map<String, WildBotAbstractPlugin> getPlugins() {
        return plugins;
    }

    public static void loadPlugins() {
        final File[] files = loadJarFiles();
        for (File file : files) {
            queueJarIfPlugin(file);
        }
    }

    private static File[] loadJarFiles() {
        final File folder = new File(PLUGINS_FOLDER);
        if (!folder.isDirectory() && !folder.exists()) if (folder.mkdirs()) Tracer
                .info("\"/plugins\" folder has been created");

        final List<File> files = new ArrayList<>();
        File[] filesArray = folder.listFiles();

        if (filesArray == null) {
            Tracer.error("List of plugins happened to be null, aborting loading of plugins");
            return (File[]) files.toArray();
        }

        Tracer.info("List of plugins happened to be null, aborting loading of plugins");

        files.addAll(Arrays.asList(filesArray));

        filesArray = new File[files.size()];

        int i = 0;
        for (File file : files) {
            if (file.isDirectory()) continue;
            if (FilenameUtils.isExtension(file.getName(), "jar")) {
                Tracer.info("Successfully found plugin \"" + file.getName() + "\"");
                filesArray[i] = file;
                i++;
            }
        }

        final StringBuilder filesNames = new StringBuilder(AnsiCodes.FG_YELLOW);

        for (i = 0; i < filesArray.length; i++) filesNames.append(filesArray[i].getName())
                .append((i < filesArray.length - 1) ? ", " : AnsiCodes.RESET);

        Tracer.info("Successfully found " + filesArray.length + " JarFiles in directory:", filesNames);

        return filesArray;
    }

    private static void queueJarIfPlugin(File file) {
        final JarFile jarFile;
        try {
            jarFile = new JarFile(file);
        } catch (IOException e) {
            Tracer.error("An exception occurred while trying to load plugin \""
                    + file.getName() + "\":", e.getCause());
            return;
        }

        final Enumeration<JarEntry> jarEntries = jarFile.entries();

        final URLClassLoader classLoader;
        try {
            classLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});
        } catch (MalformedURLException e) {
            Tracer.error("An exception occureed while trying to load a plugin:", e.getCause());
            return;
        }

            while (jarEntries.hasMoreElements()) {
            final JarEntry jarEntry = jarEntries.nextElement();
            Tracer.info("JarEntry: " + jarEntry);
            if (FilenameUtils.isExtension(jarEntry.getName(), "class")) {
                Tracer.info("IsClass");
                try {
                    Tracer.info(jarEntry.getName().replace('/', '.'));
                    final Class jarClass = classLoader.loadClass(jarEntry.getName()
                            .replace('/', '.').substring(0, jarEntry.getName().length() - 6));
                    Tracer.info(jarClass.toString());

                    if (WildBotAbstractPlugin.class.isAssignableFrom(jarClass)) {
                        Tracer.info(AnsiCodes.FG_GREEN + "Is assignable" + AnsiCodes.RESET);
                    } else Tracer.info(AnsiCodes.FG_RED + "Is not assignable" + AnsiCodes.RESET);
                } catch (ClassNotFoundException todo) {
                    Tracer.warn(todo.getCause());
                } catch (NoClassDefFoundError e) {
                    Tracer.error("Unknown dependency found at class \"" + jarEntry.getName() + "\"");
                }
            }
        }

        try {
            classLoader.close();
        } catch (IOException e) {
            Tracer.error("An exception occurred while trying to close ClassLoader");
        }
    }

    /*private static void queueJarIfPlugin(File file) {

    }*/
}
