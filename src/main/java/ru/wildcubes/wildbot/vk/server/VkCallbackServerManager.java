package ru.wildcubes.wildbot.vk.server;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.groups.responses.SetCallbackServerResponse;
import com.vk.api.sdk.objects.groups.responses.SetCallbackServerResponseStateCode;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import ru.wildcubes.wildbot.logging.Tracer;
import ru.wildcubes.wildbot.settings.SettingsManager;
import ru.wildcubes.wildbot.vk.VkApiManager;
public class VkCallbackServerManager {
    // Server General
    private static int port;
    private static String host;
    private static HandlerCollection handlers;
    // VK API special
    private static Server server;
    private static String callbackCode;

    public static void init() throws ApiException, ClientException{
        // Shorthands
        final VkApiClient vk = VkApiManager.getVkApi();
        final GroupActor actor = VkApiManager.getActor();

        // Server Details
        port = Integer.valueOf(SettingsManager.getSetting("server-port"));
        host = "http://" + SettingsManager.getSetting("server-host") + "/";
        handlers = new HandlerCollection();

        callbackCode = vk.groups().getCallbackConfirmationCode(actor).execute().getCode();

        if(!vk.groups().getCallbackServerSettings(actor).execute().getServerUrl().equals(host)) {
            Tracer.info("Group's Callback-Url not equal");
            final String confirmationCode = vk.groups().getCallbackConfirmationCode(actor).execute().getCode();
            handlers.addHandler(new VkConfirmationCodeHandler(confirmationCode));
        }

        Tracer.info("OK: " + String.valueOf(vk.groups().setCallbackSettings(actor).messageNew(true)
                .execute()));
        handlers.addHandler(new VkCallbackRequestHandler());

        final Server server = new Server(port);
        server.setHandler(handlers);
        try {
            server.start();

            int testTries = Integer.parseInt(SettingsManager.getSetting("server-start-test-tries"));
            long testSleep = Long.parseLong(SettingsManager.getSetting("server-start-test-sleep"));
            Tracer.info("Times to try connecting: " + testTries, "Interval tries: " + testSleep);

            int i = 0;
            do {
                i++;
                Tracer.info("Performing ServerTest №" + i + " for host " + host);

                final SetCallbackServerResponse response = vk.groups().setCallbackServer(actor).serverUrl(host)
                        .execute();
                Tracer.info(response);
                Tracer.info(response.getState());
                Tracer.info(response.getStateCode());

                if (response.getStateCode() == SetCallbackServerResponseStateCode.FAILED) throw new RuntimeException(
                        "Setting Callback Server has Failed");

                if (response.getStateCode() == SetCallbackServerResponseStateCode.OK) return;

                Thread.sleep(testSleep);
            } while (i < testTries);

            server.join();
        } catch (Exception e) {
            Tracer.error("An exception occurred while trying to Start:");
            e.getCause();
        }

        Tracer.info(vk.groups().getCallbackServerSettings(actor).execute());
    }
}
