/*
 * Copyright 2017 Peter P. (JARvis PROgrammer)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.wildbot.wildbotcore.util;

import io.netty.util.internal.UnstableApi;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.Random;

@UtilityClass
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
                val hostPort = Integer.parseInt(host.substring(portSeparatorIndex + 1));

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

    private static final Random RANDOM = new Random();

    public static int validatePort(int port) {
        if (port == 0) port = RANDOM.nextInt(PORT_MAX_VALUE + 1); // Use random value if port is 0
        port = Math.abs(port); // Port must be positive
        while (port > PORT_MAX_VALUE) port -= PORT_MAX_VALUE; // Port can't be higher than 65536

        return port;
    }
}
