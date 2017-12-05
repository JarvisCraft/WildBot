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

package ru.wildbot.core.secure.googleauth;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.val;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GoggleAuthManager {
    private ConcurrentMap<AuthId, AuthContainer> keys = new ConcurrentHashMap<>();

    public String newKey(final AuthId id) {
        val authenticator = new GoogleAuthenticator();
        val credentials = authenticator.createCredentials();

        val authContainer = new AuthContainer(authenticator, credentials.getKey());

        keys.put(id, authContainer);

        return authContainer.secret;
    }

    public Optional<String> getSecret(final AuthId id) {
        return Optional.ofNullable(keys.containsKey(id) ? keys.get(id).secret : null);
    }

    public AuthStatus auth(final AuthId id, final int key) {
        if (!keys.containsKey(id)) return AuthStatus.NOT_FOUND;

        val authContainer = keys.get(id);

        return authContainer.authenticator.authorize(authContainer.secret, key)
                ? AuthStatus.SUCCESS : AuthStatus.FAILURE;
    }

    /*
    public String getQr(AuthId id) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL()
    }
    */

    @EqualsAndHashCode
    @AllArgsConstructor
    private final static class AuthContainer {
        @NonNull private final GoogleAuthenticator authenticator;
        @NonNull private final String secret;
    }

    public enum AuthStatus {
        SUCCESS, FAILURE, NOT_FOUND
    }
}
