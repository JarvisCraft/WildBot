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

package ru.wildbot.core.api.event;

import lombok.Getter;
import lombok.val;
import ru.wildbot.core.console.logging.Tracer;

import java.util.*;

public class EventManager {
    @Getter private Map<Class<? extends WildBotEvent>, List<Object>> eventListeners = new HashMap<>();

    public void registerListeners(Class<? extends WildBotEvent> event, Object... listeners) {
        registerEventIfAbsent(event);
        eventListeners.get(event).addAll(Arrays.asList(listeners));
    }

    public void unregisterListeners(Class<? extends WildBotEvent> event, Object... listeners) {
        if (!eventListeners.containsKey(event)) return;
        eventListeners.get(event).removeAll(Arrays.asList(listeners));
    }

    public void registerEvents(Class<? extends WildBotEvent>... events) {
        for (val event : events) registerEventIfAbsent(event);
    }

    public void unregisterEvents(Class<? extends WildBotEvent>... events) {
        for (val event : events) eventListeners.remove(event);
    }

    public void callEvents(WildBotEvent... events) {
        for (val event : events) {
            // Register if not (for further usage)
            registerEventIfAbsent(event.getClass());

            // Queue
            val listener = eventListeners.get(event.getClass());
            val handlers = new EventListenersQueue(event, listener).getHandlers();
            for (val handler : handlers) try {
                val method = handler.getFirst().getFirst();
                val accessible = method.isAccessible();

                method.setAccessible(true);
                method.invoke(handler.getFirst().getSecond(), event);
                method.setAccessible(accessible);
            } catch (final Throwable e) {
                Tracer.error("An exception occurred while trying to call event:", e);
            }
        }
    }

    private void registerEventIfAbsent(Class<? extends WildBotEvent> event) {
        eventListeners.putIfAbsent(event, new ArrayList<>());
    }
}
