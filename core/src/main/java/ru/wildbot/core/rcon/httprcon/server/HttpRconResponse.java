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

package ru.wildbot.core.rcon.httprcon.server;

import lombok.*;
import ru.wildbot.core.data.json.EmptyObject;

@AllArgsConstructor
@RequiredArgsConstructor
public class HttpRconResponse implements Cloneable {

    ///////////////////////////////////////////////////////////////////////////
    // Constant values
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 0-valued {@link short} used as a default {@link #errorMessage} meaning no error.
     */
    public static final short DEFAULT_ERROR_CODE = 0;

    /**
     * Empty {@link String} used as a default {@link #errorMessage} meaning no error.
     */
    public static final String DEFAULT_ERROR_MESSAGE = "";

    /**
     * 1-valued {@link short} used as an {@link #errorMessage} meaning that given key-hash is wrong.
     */
    public static final short KEY_HASH_ERROR_ID = 1;

    /**
     * Self-explanatory {@link String} used as an {@link #errorMessage} meaning that given key-hash is wrong.
     */
    public static final String KEY_HASH_ERROR_MESSAGE = "Bad key-hash";

    /**
     * 2-valued {@link short} used as an {@link #errorMessage} meaning that given content-hash is wrong.
     */
    public static final short CONTENT_HASH_ERROR_ID = 2;

    /**
     * Self-explanatory {@link String} used as an {@link #errorMessage} meaning that given content-hash is wrong.
     */
    public static final String CONTENT_HASH_ERROR_MESSAGE = "Bad content-hash";

    ///////////////////////////////////////////////////////////////////////////
    // Main methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * ID of an error if it occurred.
     * Leave unchanged or use {@link #DEFAULT_ERROR_CODE} if none occurred.
     */
    @Getter @Setter public short errorCode = DEFAULT_ERROR_CODE;

    /**
     * Message of an error if it occurred. It is a custom explanation of the problem which you set as you want.
     * Leave unchanged or use {@link #DEFAULT_ERROR_MESSAGE} if none occurred.
     */
    @Getter @Setter public String errorMessage = DEFAULT_ERROR_MESSAGE;

    /**
     * The content of the response which will be serialised as JSON.
     */
    @NonNull @Getter @Setter public Object content;

    @Override
    protected HttpRconResponse clone() throws CloneNotSupportedException {
        return (HttpRconResponse) super.clone();
    }

    /**
     * Gets default response which is the one with {@link #errorCode} and {@link #errorMessage} being default
     * and {@link #content} being default.
     * @return default response
     */
    public static HttpRconResponse getDefault() {
        return new HttpRconResponse(new EmptyObject());
    }

    /**
     * Gets default response which is the one with {@link #errorCode} and {@link #errorMessage} being default
     * and {@link #content} being default
     * @return default response
     */
    public static HttpRconResponse getKeyHashError() {
        return new HttpRconResponse(KEY_HASH_ERROR_ID, KEY_HASH_ERROR_MESSAGE, new EmptyObject());
    }

    /**
     * Gets default response which is the one with {@link #errorCode} and {@link #errorMessage} being default
     * and {@link #content} being default
     * @return default response
     */
    public static HttpRconResponse getContentHashError() {
        return new HttpRconResponse(CONTENT_HASH_ERROR_ID, CONTENT_HASH_ERROR_MESSAGE, new EmptyObject());
    }
}
