package ru.wildbot.bootstrap.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;

@Log4j2
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

    public void start(final File directory) throws IOException {
        if (directory == null) throw new NullPointerException("directory");
        if (!directory.isDirectory()) throw new RuntimeException(
                new NotDirectoryException("Given path is not directory"));

        val coreFile = new File(directory, settings.getFileName());
        if (!coreFile.exists() || coreFile.isDirectory() || FileUtils.contentEquals(this.coreFile, coreFile)) FileUtils
                .copyFile(this.coreFile, coreFile);

        val command = new ArrayList<String>(settings.getStartCommand()) {{
            add("--wildbot-bootstrap");
        }};

        val processBuilder = new ProcessBuilder(command);
        processBuilder.directory(directory);
        val process = processBuilder.start();

        new CoreInputStreamReader(process.getInputStream()) {{
            setName("Core-IO-Reader");
        }}.start();
        new CoreInputStreamReader(process.getErrorStream()) {{
            setName("Core-Err-Reader");
        }}.start();
    }


    private class CoreInputStreamReader extends Thread {
        @NonNull private final BufferedReader reader;

        private CoreInputStreamReader(final InputStream inputStream) {
            reader = new BufferedReader(new InputStreamReader(inputStream));

            setDaemon(true);
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                }
            } catch (IOException e) {
                // TODO: 08.12.2017
            }
        }
    }
}
