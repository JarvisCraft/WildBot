package ru.wildbot.bootstrap;

import lombok.val;
import ru.wildbot.bootstrap.core.CoreManager;
import ru.wildbot.bootstrap.core.CoreManagerSettings;
import ru.wildbot.core.data.json.JsonDataManager;
import ru.wildbot.core.data.json.JsonNotPresentException;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * The main class which plays the role of startpoint for WildBot
 */
public class WildBotBootstrap {
    public static void main(String[] args) {
        // TODO: 05.12.2017 Multi core
        //new WildBotCore().main(args);
        try {
            val manager = new CoreManager(JsonDataManager
                    .readAndWrite(new File("core/settings.yml"), CoreManagerSettings.class)
                    .orElseThrow(JsonNotPresentException::new)).init();
            manager.start(new File("core/Bot-1/") {{
                if (!exists()) mkdirs();
            }});
        } catch (JsonNotPresentException e) {
            // TODO: 08.12.2017
            e.printStackTrace();
        } catch (IOException e) {
            // TODO: 08.12.2017
            e.printStackTrace();
        }


        new Scanner(System.in).nextLine();
    }
}
