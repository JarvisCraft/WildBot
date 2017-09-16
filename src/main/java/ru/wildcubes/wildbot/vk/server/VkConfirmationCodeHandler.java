package ru.wildcubes.wildbot.vk.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vk.api.sdk.callback.objects.messages.CallbackMessage;
import com.vk.api.sdk.callback.objects.messages.CallbackMessageType;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import ru.wildcubes.wildbot.console.logging.Tracer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class VkConfirmationCodeHandler extends AbstractHandler {
    private String confirmationCode;
    private static Gson gson = new Gson();

    public VkConfirmationCodeHandler(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {
        if (request == null || request.getMethod() == null
                || !request.getMethod().equalsIgnoreCase("POST")) return;

        Tracer.info("VkConfirmationCodeHandler");

        final CallbackMessage<JsonObject> callback = gson.fromJson(
                request.getReader().lines().collect(Collectors.joining()),
                new TypeToken<CallbackMessage<JsonObject>>(){}.getType()
        );

        if (callback == null || callback.getType() != CallbackMessageType.CONFIRMATION) return;

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println(confirmationCode);
    }
}
