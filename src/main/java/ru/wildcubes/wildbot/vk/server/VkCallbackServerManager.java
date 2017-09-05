package ru.wildcubes.wildbot.vk.server;

import com.vk.api.sdk.actions.Groups;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.groups.CallbackServer;
import com.vk.api.sdk.objects.groups.responses.GetCallbackServersResponse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import ru.wildcubes.wildbot.logging.Tracer;
import ru.wildcubes.wildbot.settings.SettingsManager;
import ru.wildcubes.wildbot.vk.VkApiManager;

public class VkCallbackServerManager {
    // Server General
    private static int port;
    private static String host;
    private static Server server;
    private static HandlerCollection handlers;
    private static int id;
    // VK API special
    private static CallbackServer callbackServer = null;
    private static String confirmationCode;

    public static void init() throws ApiException, ClientException {
        // Shorthands
        final Groups group = VkApiManager.getVkApi().groups();
        final GroupActor actor = VkApiManager.getActor();

        // Server Details
        // Used for Opening Jetty Server
        port = Integer.valueOf(SettingsManager.getSetting("callback-server-port"));
        // Used for opening Jetty Server and as Callback Url
        host = SettingsManager.getSetting("callback-server-host");

        Tracer.info("Using Host \"" + host + "\" for Callback Server");
        handlers = new HandlerCollection();

        confirmationCode = group.getCallbackConfirmationCode(actor).execute().getCode();

        final ContextHandler contextHandler = new ContextHandler("/wildbot");

        handlers.addHandler(contextHandler);
        handlers.addHandler(new VkConfirmationCodeHandler(confirmationCode));
        handlers.addHandler(new VkCallbackRequestHandler());

        server = new Server(port);
        server.setHandler(handlers);

        // Find CallBack server in Group's list

        try {
            server.start();

            findCallbackServer(group, actor);
            registerCallbackServer(group, actor);

            group.setCallbackSettings(actor, id).messageNew(true).execute();

            server.join();
        } catch (Exception e) {//TODO
            e.printStackTrace();
        }

        /*
        if(!vk.groups().getCalSe(actor, id).execute().equals(host)) {
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

                final SetCallbackServerResponse response = vk.groups().addCallbackServer(actor).serverUrl(host)
                        .execute();
                new
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
        */

        //Tracer.info(group.getCallbackServerSettings(actor).execute());
    }

    public static void findCallbackServer(Groups group, GroupActor actor) throws ApiException, ClientException {
        Tracer.info("Finding CallBack Server in the list of registered");

        final GetCallbackServersResponse servers = group.getCallbackServers(actor).execute();

        for (CallbackServer callbackServerTested : servers.getItems()) if (callbackServerTested.getUrl()
                .equalsIgnoreCase(host)) {
            Tracer.info("CallbackServer was found by host " + host);
            callbackServer = callbackServerTested;
            id = callbackServer.getId();
            break;
        }
    }

    public static void registerCallbackServer(Groups group, GroupActor actor) throws ApiException, ClientException {
        Tracer.info("Registering custom Callback Server");

        if (callbackServer == null) {
            Tracer.info("There were no registered CallbackServer with host " + host);
            id = group.addCallbackServer(actor, host, SettingsManager.getSetting("callback-server-title"))
                    .execute().getServerId();
            findCallbackServer(group, actor);
        }
    }
}
