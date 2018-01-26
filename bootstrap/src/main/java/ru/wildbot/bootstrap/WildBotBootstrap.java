package ru.wildbot.bootstrap;

import lombok.Getter;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import ru.wildbot.bootstrap.core.CoreListFile;
import ru.wildbot.bootstrap.core.CoreManager;
import ru.wildbot.bootstrap.core.CoreManagerSettings;
import ru.wildbot.bootstrap.core.CoreSettings;
import ru.wildbot.core.console.logging.Tracer;
import ru.wildbot.core.data.json.JsonDataManager;
import ru.wildbot.core.data.json.JsonNotPresentException;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * The main class which plays the role of startpoint for WildBot
 */
public class WildBotBootstrap {
    @Getter private static CoreListFile cores;

    public static void main(String[] args) {
        Tracer.setLogger(LogManager.getLogger("Bootstrap"));

        try {
            cores = JsonDataManager
                    .readAndWrite(new File("core/cores.json"), CoreListFile.class)
                    .orElseThrow(JsonNotPresentException::new);
        } catch (JsonNotPresentException e) {

            e.printStackTrace();
        }

        // TODO: 05.12.2017 Multi core
        //new WildBotCore().main(args);
        try {
            val manager = new CoreManager(JsonDataManager
                    .readAndWrite(new File("core/settings.json"), CoreManagerSettings.class)
                    .orElseThrow(JsonNotPresentException::new)).init();
            manager.start(new File("core/Bot-1/") {{
                if (!exists()) mkdirs();
            }}, new CoreSettings());
        } catch (JsonNotPresentException e) {
            // TODO: 08.12.2017
            e.printStackTrace();
        } catch (IOException e) {
            // TODO: 08.12.2017
            e.printStackTrace();
        }

        val scanner = new Scanner(System.in);
        while (scanner.hasNext()) {

        }
    }
}
