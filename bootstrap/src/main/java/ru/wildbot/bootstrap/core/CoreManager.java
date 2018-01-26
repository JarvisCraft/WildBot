package ru.wildbot.bootstrap.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.Arrays;

@RequiredArgsConstructor
public class CoreManager {
    @NonNull private final CoreManagerSettings settings;

    private File coreFile;

    public CoreManager init() throws IOException {
        coreFile = new File(settings.getPath(), settings.getFileName());

        if (!coreFile.exists() || coreFile.isDirectory()) FileUtils
                .copyURLToFile(new URL(settings.getDownloadUrl()), coreFile);

        return this;
    }

    public CoreManager start(final File directory, final CoreSettings coreSettings) throws IOException {
        if (directory == null) throw new NullPointerException("directory");
        if (!directory.isDirectory()) throw new RuntimeException(
                new NotDirectoryException("Given path is not directory"));

        val coreFile = new File(directory, settings.getFileName());
        if (!coreFile.exists() || coreFile.isDirectory() || FileUtils.contentEquals(this.coreFile, coreFile)) FileUtils
                .copyFile(this.coreFile, coreFile);

        val command = new ArrayList<String>(settings.getStartCommand()) {{
            add("--wildbot-bootstrap");
            addAll(Arrays.asList(coreSettings.getCommandArguments()));
        }};

        val processBuilder = new ProcessBuilder(command);
        processBuilder.directory(directory);
        new CoreProcess(processBuilder, coreSettings.getName(), coreSettings.getLogSize());

        return this;
    }
}
