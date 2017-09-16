package ru.wildcubes.wildbot.api.plugin;

import lombok.*;
import ru.wildcubes.wildbot.console.logging.AnsiCodes;
import ru.wildcubes.wildbot.console.logging.Tracer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
@ToString(exclude = {"jarFile", "data", "pluginsClasses"})
class JavaPluginInQueue {
    @NonNull @Getter private final File file;
    @NonNull @Getter private final JarFile jarFile;

    @NonNull @Getter @Setter private List<String> mainClasses;
    @NonNull @Getter @Setter private List<String> dependencies;
    @NonNull @Getter @Setter private List<String> softDependencies;
    @NonNull @Getter @Setter private List<String> loadBefore;

    @Getter @Setter(value = AccessLevel.PRIVATE) private WildBotPluginData data;


    void loadClasses() {
        final URLClassLoader classLoader;
        try {
            classLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});
        } catch (MalformedURLException e) {
            Tracer.error("An exception occureed while trying to load a plugin: ", e.getCause());
            return;
        }

        for (String mainClass : mainClasses) {
            try {
                final Class<?> jarClass = classLoader.loadClass(mainClass);
                Tracer.info(jarClass.toString());

                if (WildBotJavaPlugin.class.isAssignableFrom(jarClass)) {
                    if (jarClass.isAnnotationPresent(WildBotPluginData.class)) {
                        Tracer.info(AnsiCodes.FG_GREEN + "Registering Class " + jarClass.getSimpleName()
                                + AnsiCodes.RESET);
                        pluginsClasses.add(jarClass.asSubclass(WildBotJavaPlugin.class));

                        Tracer.info(AnsiCodes.FG_GREEN + "Class " + jarClass.getSimpleName()
                                + "has been registered" + AnsiCodes.RESET);
                    } else Tracer.warn("Unable to load plugin's class \"" + mainClass
                            + "\": not annotated with @WildBotPluginData");
                } else Tracer.warn("Unable to load plugin's class \"" + mainClass
                        + "\": not extending WildBotAbstractPlugin");
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                Tracer.warn("Unable to load Class \"" + mainClass + "\" as it can't be found");
            }
        }

        try {
            classLoader.close();
        } catch (IOException e) {
            Tracer.error("Unable to close ClassLoader while loading one of the plugins: " + e.getCause());
        }
    }
    
    @Getter @Setter private Set<Class<? extends WildBotJavaPlugin>> pluginsClasses = new LinkedHashSet<>();
    String getJarName() {
        final String fileName = file.getName();
        return fileName.substring(7, fileName.length()-4);
    }
}
