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

package ru.wildbot.core.provider;

import lombok.Getter;
import ru.wildbot.core.api.manager.WildBotManager;

import java.util.HashMap;
import java.util.Map;

public class ProviderManager implements WildBotManager {
    @Getter private boolean enabled = false;

    private Map<Class<? extends AbstractProvider>, AbstractProvider> providers = new HashMap<>();

    @Override
    public void enable() throws Exception {
        checkEnabled();

        enabled = true;
    }

    @Override
    public void disable() throws Exception {
        checkDisabled();
        // TODO: 21.10.2017
        enabled = false;
    }

    public boolean isRegistered(final Class<? extends AbstractProvider> provider) {
        return providers.containsKey(provider);
    }


    public AbstractProvider register(final Class<? extends AbstractProvider> providerType,
                                     final AbstractProvider provider, final boolean force) {
        if (force) return providers.put(providerType, provider);
        else return providers.putIfAbsent(providerType, provider);
    }

    public void registerEmpty(final Class<? extends AbstractProvider> providerType) {
        providers.put(providerType, null);
    }

    public AbstractProvider register(final Class<? extends AbstractProvider> providerType,
                                     final AbstractProvider provider) {
        return register(providerType, provider, false);
    }

    public AbstractProvider get(final Class<? extends AbstractProvider> providerType) {
        return providers.get(providerType);
    }


    public AbstractProvider getOrDefault(final Class<? extends AbstractProvider> providerType,
                                         final AbstractProvider defaultProvider) {
        return providers.getOrDefault(providerType, defaultProvider);
    }

    public AbstractProvider getOrRegister(final Class<? extends AbstractProvider> providerType,
                                          final AbstractProvider provider) {
        providers.putIfAbsent(providerType, provider);
        return providers.get(providerType);
    }
}
