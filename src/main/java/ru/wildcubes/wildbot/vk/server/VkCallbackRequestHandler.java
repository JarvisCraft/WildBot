package ru.wildcubes.wildbot.vk.server;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import ru.wildcubes.wildbot.console.logging.Tracer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Handles the Incoming Callback HTTP-response from VK servers
 */
public class VkCallbackRequestHandler extends AbstractHandler {
    private static final String OK_RESPONSE = "ok";
    private VkCallbackApiHandler callbackApiHandler = new VkCallbackApiHandler();

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {
        Tracer.info("VkCallbackRequestHandler");

        if (baseRequest.isHandled()) return;

        if (request == null || request.getMethod() == null
                || !request.getMethod().equalsIgnoreCase("POST")) return;

        final String body = request.getReader().lines().collect(Collectors.joining());
        Tracer.info("VkCallbackRequestHandler2");

        //todo check

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println(OK_RESPONSE);
    }
}
