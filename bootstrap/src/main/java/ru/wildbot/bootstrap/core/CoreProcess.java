package ru.wildbot.bootstrap.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.wildbot.core.console.logging.Tracer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@RequiredArgsConstructor
public class CoreProcess {
    @NonNull private final String name;
    @NonNull private final Process process;
    @NonNull private final CoreInputStreamReader inputStreamReader;
    @NonNull private final CoreInputStreamReader errorStreamReader;
    @NonNull @Getter private final FixedSizeLog log;

    @Getter private boolean active;

    private CoreProcess activate() {
        log.getLines();
        return this;
    }

    private CoreProcess deactivate() {
        return this;
    }

    public CoreProcess(@NonNull final ProcessBuilder processBuilder, @NonNull final String name, final int logSize)
            throws IOException {
        this.name = name;
        process = processBuilder.start();
        inputStreamReader = new CoreInputStreamReader(process.getInputStream());
        errorStreamReader = new CoreInputStreamReader(process.getErrorStream());
        log = new FixedSizeLog(logSize);
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
                    log.add(line);
                    if (active) Tracer.info();
                }
            } catch (IOException e) {
                // TODO: 08.12.2017
            }
        }
    }
}
