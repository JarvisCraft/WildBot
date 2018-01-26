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

package ru.wildbot.core.api.plugin;

import lombok.*;
import ru.wildbot.core.api.plugin.annotation.WildBotPluginData;
import ru.wildbot.core.console.logging.AnsiCodes;
import ru.wildbot.core.console.logging.Tracer;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

@ToString(exclude = {"jarFile", "pluginsClasses"})
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class JavaPluginInQueue {
    @NonNull @Getter private final File file;
    @NonNull @Getter private final JarFile jarFile;

    @NonNull @Getter @Setter private List<String> mainClasses;
    @NonNull @Getter @Setter private List<String> dependencies;
    @NonNull @Getter @Setter private List<String> softDependencies;
    @NonNull @Getter @Setter private List<String> loadBefore;

    @Getter @Setter(value = AccessLevel.PRIVATE)
    private WildBotPluginData data;

    void loadClasses() {
        try {
            injectFile(file);
        } catch (Exception e) {
            Tracer.error("An error occurred while trying to inject file \"" + file.getName() + "\":", e);
        }

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        for (String mainClass : mainClasses) {
            try {
                val jarClass = classLoader.loadClass(mainClass);
                Tracer.info(jarClass.toString());

                if (WildBotJavaPlugin.class.isAssignableFrom(jarClass)) {
                    if (jarClass.isAnnotationPresent(WildBotPluginData.class)) {
                        Tracer.info(AnsiCodes.FG_GREEN + "Registering Class " + jarClass.getSimpleName()
                                + AnsiCodes.RESET);
                        pluginsClasses.add(jarClass.asSubclass(WildBotJavaPlugin.class));

                        Tracer.info(AnsiCodes.FG_GREEN + "Class " + jarClass.getSimpleName()
                                + " has been registered" + AnsiCodes.RESET);
                    } else Tracer.warn("Unable to load plugin's class \"" + mainClass
                            + "\": not annotated with @WildBotPluginData");
                } else Tracer.warn("Unable to load plugin's class \"" + mainClass
                        + "\": not extending WildBotAbstractPlugin");
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                Tracer.warn("Unable to load Class for name \"" + mainClass + "\" as it can't be found");
            }
        }

    }

    @Getter @Setter private Set<Class<? extends WildBotJavaPlugin>> pluginsClasses = new LinkedHashSet<>();

    String getJarName() {
        val fileName = file.getName();
        return fileName.substring(0, fileName.length() - 4);
    }

    private static void injectFile(final File file) throws Exception {
        val method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        val accessible = method.isAccessible();

        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), file.toURI().toURL());
        method.setAccessible(accessible);

    }
}
