package ru.wildcubes.wildbot.vk;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.GroupFull;
import lombok.Getter;
import lombok.Setter;
import ru.wildcubes.wildbot.logging.Tracer;
import ru.wildcubes.wildbot.settings.SettingsManager;

public class VkApiManager {
    @Getter private static final VkApiClient vkApi = new VkApiClient(new HttpTransportClient());

    @Getter @Setter private static GroupActor actor;
    @Getter @Setter private static GroupFull group;

    ///////////////////////////////////////////////////////////////////////////
    // Secure
    ///////////////////////////////////////////////////////////////////////////

    private static String GROUP_KEY;

    public static final String HELLO_WORLD = "Hello World!\n\nInitializing Wildbot:" +
            "\nName: ${name}\nVersion: ${version}\nProtocol: WildBot-CustomProtocol\nSystemTime: "
            + System.currentTimeMillis();

    public static void authorise() {
        final int GROUP_ID = Integer.parseInt(SettingsManager.getSetting("group-id"));
        GROUP_KEY = SettingsManager.getSetting("group-key");

        try {
            actor = new GroupActor(GROUP_ID, GROUP_KEY);

            group = vkApi.groups().getById(actor).groupId("wild_cubes").execute().get(0);

            Tracer.info("Group \"" + group.getName() + "\" has been successfully authorised:" +
                    "by the following criteria:",
                    "ID: " + GROUP_ID, "Key: " + GROUP_KEY);

            Tracer.info("Send: " + vkApi.messages().send(actor).userId(402833125).message(HELLO_WORLD).execute());

            vkApi.messages().send(actor).userId(402833125).message("Эээй, у меня вообще-то чувсива есть :(").execute();
        } catch (ApiException | ClientException | IndexOutOfBoundsException e) {
            Tracer.error("Unable to authorise VK.API, maybe wrong Group-ID / Group-Key was given:",
                    e.getCause());
        }
    }
}