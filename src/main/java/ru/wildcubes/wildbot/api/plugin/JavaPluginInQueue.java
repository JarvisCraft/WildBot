package ru.wildcubes.wildbot.api.plugin;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;

class JavaPluginInQueue {
    @Getter private final File file;
    @Getter private final JarFile jarFile;
    @Getter @Setter private List<String> dependencies = new ArrayList<>();
    @Getter @Setter private List<String> softDependencies = new ArrayList<>();

    public JavaPluginInQueue(File file, JarFile jarFile, String[] dependencies, String[] softDependencies) {
        this.file = file;
        this.jarFile = jarFile;
        Collections.addAll(this.dependencies, dependencies);
        Collections.addAll(this.softDependencies, softDependencies);
    }
}
