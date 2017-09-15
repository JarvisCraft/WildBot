package ru.wildcubes.wildbot.api.plugin;

import lombok.*;
import ru.wildcubes.wildbot.api.plugin.annotation.WildBotPluginData;
import ru.wildcubes.wildbot.logging.AnsiCodes;
import ru.wildcubes.wildbot.logging.Tracer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.jar.JarFile;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
class JavaPluginInQueue {
    @NonNull @Getter private final File file;
    @NonNull @Getter private final JarFile jarFile;

    @NonNull @Getter @Setter private List<String> mainClasses;
    @NonNull @Getter @Setter private List<String> dependencies;
    @NonNull @Getter @Setter private List<String> softDependencies;

    @Getter @Setter(value = AccessLevel.PRIVATE) private WildBotPluginData data;


    public void loadClasses() {
        final URLClassLoader classLoader;
        try {
            classLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});
        } catch (MalformedURLException e) {
            Tracer.error("An exception occureed while trying to load a plugin: ", e.getCause());
            return;
        }

        for (String mainClass : mainClasses) {
            try {
                final Class jarClass = classLoader.loadClass(mainClass);
                Tracer.info(jarClass.toString());

                if (WildBotAbstractPlugin.class.isAssignableFrom(jarClass)) {
                    if (jarClass.isAnnotationPresent(WildBotPluginData.class)) {
                        Tracer.info(AnsiCodes.FG_GREEN + "Loading Class " + jarClass.getSimpleName()
                                + AnsiCodes.RESET);
                    } else Tracer.warn("Unable to load plugin's class \"" + mainClass
                            + "\": not annotated with @WildBotPluginData");
                } else Tracer.warn("Unable to load plugin's class \"" + mainClass
                        + "\": not extending WildBotAbstractPlugin");
            } catch (ClassNotFoundException e) { // TODO
                Tracer.warn(e.getCause());
            } catch (NoClassDefFoundError e) {
                Tracer.error("Unknown dependency found at class \"" + mainClass + "\"");
            }
        }

        try {
            classLoader.close();
        } catch (IOException e) {
            Tracer.error("Unable to close ClassLoader while loading one of the plugins: " + e.getCause());
        }
    }
}
