package ru.wildcubes.wildbot.vk.server;

import com.vk.api.sdk.actions.Groups;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiCaptchaException;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.groups.CallbackServer;
import com.vk.api.sdk.objects.groups.responses.GetCallbackServersResponse;
import org.eclipse.jetty.server.Server;
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

        final  VkConfirmationCodeHandler confirmationCodeHandler = new VkConfirmationCodeHandler(confirmationCode);
        handlers.addHandler(confirmationCodeHandler);
        handlers.addHandler(new VkCallbackRequestHandler());

        server = new Server(port);
        server.setHandler(handlers);

        // Find CallBack server in Group's list

        try {
            server.start();

            findCallbackServer(group, actor);
            registerCallbackServer(group, actor);
            confirmationCodeHandler.setConfirmationCode(group.getCallbackConfirmationCode(actor).execute().getCode());

            group.setCallbackSettings(actor, id).messageNew(true).execute();

            server.join();
        } catch (Exception e) {//TODO
            Tracer.info("An exception occurred while Starting VK-Callback Server:", e.getCause());
        }
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
