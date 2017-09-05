package ru.wildcubes.wildbot.util;

import io.netty.util.internal.UnstableApi;

public class DataFormatter {
    public static final String HTTP_PREFIX = "http://";
    public static final String HTTPS_PREFIX = "https://";
    public static final String BACKSLASH = "/";
    public static final char PORT_SEPARATOR = ':';
    public static final int PORT_MAX_VALUE = 65536;

    // Format:
    // <scheme(protocol)>:[//[<login>:<password>@]<host>[:<port>]][/]<URL-path>[?<params>][#<anchor>]

    public static String toUrl(String host) {
        // Protocol
        if (!host.startsWith(HTTPS_PREFIX) && !host.startsWith(HTTP_PREFIX)) host = HTTP_PREFIX + host;

        // Backslash
        if (!host.endsWith(BACKSLASH)) host += BACKSLASH;

        return host;
    }

    public static String toRootHttpUrl(String host, Integer port) {
        // Protocol
        if (!host.startsWith(HTTPS_PREFIX) && !host.startsWith(HTTP_PREFIX)) host = HTTP_PREFIX + host;

        // Backslash
        if (!host.endsWith(BACKSLASH)) host += BACKSLASH;

        // Port
        if (port != null) {
            port = validatePort(port);

            final int portSeparatorIndex = host.lastIndexOf(PORT_SEPARATOR);
            if (portSeparatorIndex <= 0) host = new StringBuilder(host)
                    .insert(host.length() - 1, PORT_SEPARATOR + port).toString();
            else try {
                //Check if is port
                final int hostPort = Integer.parseInt(host.substring(portSeparatorIndex, host.length() - 1));

                // Ports not equal, given has higher priority
                if (hostPort != port) host = new StringBuilder()
                        .replace(portSeparatorIndex, host.length() - 1, port.toString()).toString();

            } catch (NumberFormatException e) {
                // No port given
                host = new StringBuilder(host).insert(host.length() - 1, PORT_SEPARATOR)
                        .insert(host.length(), port).toString();
            }
        }

        return host;
    }

    @UnstableApi
    @Deprecated
    public static String toVkHttpUrl(String host, Integer port) {
        // Protocol
        if (!host.startsWith(HTTPS_PREFIX) && !host.startsWith(HTTP_PREFIX)) host = HTTP_PREFIX + host;

        // Backslash remove
        if (host.endsWith(BACKSLASH)) host = host.substring(0, host.length() - 1);

        // Port
        if (port != null) {
            port = validatePort(port);

            final int portSeparatorIndex = host.lastIndexOf(PORT_SEPARATOR);

            if (portSeparatorIndex <= 0) host += (PORT_SEPARATOR + port);
            else try { //Check if is port
                final int hostPort = Integer.parseInt(host.substring(portSeparatorIndex + 1));

                // Ports not equal, given as method param has higher priority
                if (hostPort != port) host = host.substring(0, portSeparatorIndex + 1) + port;

            } catch (NumberFormatException e) { // No port given
                /*host = new StringBuilder(host).insert(host.length() - 1, PORT_SEPARATOR)
                        .insert(host.length(), port).toString();*/
                host = host + PORT_SEPARATOR + port;
            }

            // Backslash remove
            host = new StringBuilder(host).insert(host.lastIndexOf(PORT_SEPARATOR), BACKSLASH).toString();
        } else host += BACKSLASH;

        return host;
    }

    public static int validatePort(int port) {
        if (port == 0) port = (int) (Math.random() * PORT_MAX_VALUE) + 1; // Use random value if port is 0
        port = Math.abs(port); // Port must be positive
        while (port > PORT_MAX_VALUE) port -= PORT_MAX_VALUE; // Port can't be higher than 65536

        return port;
    }
}
