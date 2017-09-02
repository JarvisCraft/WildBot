package ru.wildcubes.wildbot.vk;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.GroupFull;
import ru.wildcubes.wildbot.logging.Tracer;
import ru.wildcubes.wildbot.settings.SettingsManager;

public class VkApiManager {
    private static VkApiClient vkApi = new VkApiClient(new HttpTransportClient());

    private static Integer GROUP_ID;
    private static String GROUP_KEY;

    private static GroupActor actor;
    private static GroupFull group;

    public static VkApiClient getVkApi() {
        return vkApi;
    }

    public static GroupActor getActor() {
        return actor;
    }

    public static GroupFull getGroup() {
        return group;
    }

    public static void authorise() {
        GROUP_ID = Integer.parseInt(SettingsManager.getSetting("group-id"));
        GROUP_KEY = SettingsManager.getSetting("group-key");

        try {
            actor = new GroupActor(GROUP_ID, GROUP_KEY);

            group = vkApi.groups().getById(actor).groupId("wild_cubes").execute().get(0);

            Tracer.info("Group \"" + group.getName() + "\" has been successfully authorised:" +
                    "by the following criteria:",
                    "ID: " + GROUP_ID, "Key: " + GROUP_KEY);

            Tracer.info("Send: " + vkApi.messages().send(actor).userId(288451376).message("Hello World!").execute());
        } catch (ApiException | ClientException | IndexOutOfBoundsException e) {
            Tracer.error("Unable to authorise VK.API, maybe wrong Group-ID / Group-Key was given:",
                    e.getCause());
        }
    }
}