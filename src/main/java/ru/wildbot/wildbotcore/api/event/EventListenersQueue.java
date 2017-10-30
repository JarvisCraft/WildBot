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

package ru.wildbot.wildbotcore.api.event;

import lombok.Getter;
import lombok.val;
import ru.wildbot.wildbotcore.util.collection.Pair;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EventListenersQueue extends ArrayList<Class<?>> {
    @Getter private transient List<EventHandlerMethod> handlers = new ArrayList<>();

    public EventListenersQueue(final WildBotEvent event, final List<Object> eventListeners) {
        // Get all handler methods
        for (val listener : eventListeners) {
            val methods = listener.getClass().getMethods();
            for (val method : methods) if (method.isAnnotationPresent(EventHandler.class)
                    && method.getParameterCount() == 1
                    && event.getClass().isAssignableFrom(method.getParameters()[0].getType())) handlers
                    .add(new EventHandlerMethod(method, listener, method.getAnnotation(EventHandler.class).order()));
        }

        // Sort all handler methods
        sortHandlers();
    }

    private void sortHandlers() {
        for (int i = 0; i < handlers.size(); i++) for (int j = 0; j < handlers.size(); j++) {
            if (handlers.get(j).getSecond() > handlers.get(i).getSecond()) {
                val bigger = handlers.get(j);
                handlers.set(j, handlers.get(i));
                handlers.set(i, bigger);
            }
        }
    }

    public static class EventHandlerMethod extends Pair<Pair<Method, Object>, Integer> {
        public EventHandlerMethod(final Method handler, final Object listener, final Integer order) {
            super(Pair.of(handler, listener), order);
        }
    }
}
