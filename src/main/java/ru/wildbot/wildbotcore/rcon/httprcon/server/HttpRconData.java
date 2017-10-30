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

package ru.wildbot.wildbotcore.rcon.httprcon.server;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.*;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;

import static org.apache.commons.codec.digest.DigestUtils.sha512;

@ToString(exclude = {"sha512KeyHash", "sha512ContentHash"})
@EqualsAndHashCode
@NoArgsConstructor
@RequiredArgsConstructor
public class HttpRconData {

    /**
     * Key hash (SHA512) used for verification of packet
     */
    @SerializedName("key-hash") @NonNull @Getter private String keyHexHash;
    @Expose @Getter private byte[] sha512KeyHash;

    @SneakyThrows
    public HttpRconData decodeKeyHash() throws DecoderException {
        if (keyHexHash.isEmpty()) throw new DecoderException();
        sha512KeyHash = Hex.decodeHex(keyHexHash.toCharArray());
        return this;
    }

    /**
     * Main content hash (SHA512) used for verification
     */
    @SerializedName("content-hash") @NonNull @Getter private String contentHexHash;
    @Expose @Getter private byte[] sha512ContentHash;

    public HttpRconData decodeContentHash() throws DecoderException {
        if (contentHexHash.isEmpty()) throw new DecoderException();
        sha512ContentHash = Hex.decodeHex(contentHexHash.toCharArray());
        return this;
    }

    public HttpRconData decodeHashes() throws DecoderException {
        return decodeKeyHash().decodeContentHash();
    }

    /**
     * The very of HTTP-RCON content packet as JSON string
     */
    @SerializedName("content") @NonNull @Getter private String jsonContent;

    public boolean verifyContent() {
        return Arrays.equals(sha512(jsonContent), sha512ContentHash);
    }

    public boolean verifyKey(final String key) {
        return Arrays.equals(sha512(key), sha512KeyHash);
    }

    public boolean verify(final String key) {
        return verifyKey(key) && verifyContent();
    }
}
