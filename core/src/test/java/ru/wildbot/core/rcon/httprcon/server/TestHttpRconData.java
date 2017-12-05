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

import org.apache.commons.codec.DecoderException;
import org.junit.Test;
import ru.wildbot.core.test.WildBotTest;

import java.util.Arrays;

public class TestHttpRconData extends WildBotTest {
    private final static byte[] HASH_EMPTY = new byte[]{};
    private final static String STRING_EMPTY = "";
    private final static String HEX_HASH_EMPTY = "";
    private final static String HEX_HASH_MALFORMED = "nope";

    private final static byte[] KEY_HASH = new byte[]{
            -125, 53, -6, 86, -44, -121, 86, 45, -30, 72, -12, 123, -17, -57, 39, 67,
            51, 64, 81, -35, -1, -52, 44, 9, 39, 95, 102, 84, 84, -103, 3, 23,
            89, 71, 69, -18, 23, -64, -113, 121, -116, -41, -36, -32, -70, -127, 85, -36,
            -38, 20, -10, 57, -116, 29, 21, 69, 17, 101, 32, -95, 51, 1, 124, 9
    };

    private final String WRONG_HASH_HEX = "0123456789abcdef0123456789abcdef0123456789abcdef0" +
            "123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcd";

    private final String KEY_HASH_HEX = "8335fa56d487562de248f47befc72743334051ddffcc2c09275" +
            "f665454990317594745ee17c08f798cd7dce0ba8155dcda14f6398c1d1545116520a133017c09";

    private final String RIGHT_KEY = "key";
    private final String WRONG_KEY = "kappa";

    private final static byte[] CONTENT_HASH = new byte[]{
            -93, -54, -60, -18, -43, 29, -112, -67, -61, 99, -83, -46, -56, 47, 3, 85,
            -84, 126, 16, 18, -14, 116, -95, 26, -93, 20, -31, -93, 95, -69, 0, -44,
            85, -102, 67, 8, -6, 33, 80, -72, -2, 84, -72, -90, -11, 30, -111, 42,
            -42, -126, 91, 121, -71, -108, -112, -39, -7, 116, 119, 25, -81, 48, 66, -14
    };
    private final String CONTENT_HASH_HEX = "a3cac4eed51d90bdc363add2c82f0355ac7e1012f274a11aa31" +
            "4e1a35fbb00d4559a4308fa2150b8fe54b8a6f51e912ad6825b79b99490d9f9747719af3042f2";

    private final String RIGHT_CONTENT = "{json:data}";
    private final String WRONG_CONTENT = "{data:json}";

    @Test
    public void testHashing() throws Exception {
        testing("Hashing");

        testing("both hashes empty");
        assertException(new HttpRconData(HEX_HASH_EMPTY, HEX_HASH_EMPTY, RIGHT_CONTENT)
                ::decodeHashes, DecoderException.class).success();

        testing("key-hash wrong & content-hash empty");
        assertException(new HttpRconData(HEX_HASH_MALFORMED, HEX_HASH_EMPTY, RIGHT_CONTENT)
                ::decodeHashes, DecoderException.class).success();

        testing("key-hash empty & content-hash wrong");
        assertException(new HttpRconData(HEX_HASH_EMPTY, HEX_HASH_MALFORMED, RIGHT_CONTENT)
                ::decodeHashes, DecoderException.class).success();

        testing("both hashes wrong");
        assertException(new HttpRconData(HEX_HASH_MALFORMED, HEX_HASH_MALFORMED, RIGHT_CONTENT)
                ::decodeHashes, DecoderException.class).success();

        testing("wrong key-hash");
        assertException(new HttpRconData(HEX_HASH_MALFORMED, CONTENT_HASH_HEX, RIGHT_CONTENT)
                ::decodeHashes, DecoderException.class).success();

        testing("wrong content-hash");
        assertException(new HttpRconData(WRONG_HASH_HEX, HEX_HASH_MALFORMED, RIGHT_CONTENT)
                ::decodeHashes, DecoderException.class).success();

        testing("both hashes right");
        final HttpRconData data = new HttpRconData(KEY_HASH_HEX, CONTENT_HASH_HEX, RIGHT_CONTENT).decodeHashes();
        success();

        testing("key-hashes equality");
        assert Arrays.equals(data.getSha512KeyHash(), KEY_HASH);
        success();

        testing("content-hashes equality");
        assert Arrays.equals(data.getSha512ContentHash(), CONTENT_HASH);
        success();

        allSuccess();
    }

    @Test
    public void testKeyVerification() throws Exception {
        testing("Key Verification");

        final HttpRconData data = new HttpRconData(KEY_HASH_HEX, WRONG_HASH_HEX, STRING_EMPTY).decodeHashes();
        testing("equal key-hashes");
        assert data.verifyKey(RIGHT_KEY);
        success();
        testing("unequal key-hashes");
        assert !data.verifyKey(WRONG_KEY);
        success();

        allSuccess();
    }

    @Test
    public void testContentVerification() throws Exception {
        testing("Content Verification");

        HttpRconData data = new HttpRconData(WRONG_HASH_HEX, CONTENT_HASH_HEX, RIGHT_CONTENT).decodeHashes();
        testing("equal content-hashes:");
        assert data.verifyContent();
        success();

        data = new HttpRconData(WRONG_HASH_HEX, CONTENT_HASH_HEX, WRONG_CONTENT).decodeHashes();
        testing("Testing unequal content-hashes:");
        assert !data.verifyContent();
        success();

        allSuccess();
    }

    @Test
    public void testVerification() throws Exception {
        testing("Verification");

        testing("both hashes wrong");
        HttpRconData data = new HttpRconData(WRONG_HASH_HEX, WRONG_HASH_HEX, RIGHT_CONTENT).decodeHashes();
        assert !data.verify(RIGHT_KEY);
        success();

        testing("key-hash wrong");
        data = new HttpRconData(WRONG_HASH_HEX, CONTENT_HASH_HEX, RIGHT_CONTENT).decodeHashes();
        assert !data.verify(WRONG_KEY);
        success();

        testing("content-hash wrong");
        data = new HttpRconData(KEY_HASH_HEX, WRONG_HASH_HEX, RIGHT_CONTENT).decodeHashes();
        assert !data.verify(WRONG_KEY);
        success();

        testing("wrong content");
        data = new HttpRconData(WRONG_HASH_HEX, CONTENT_HASH_HEX, WRONG_CONTENT).decodeHashes();
        assert !data.verify(RIGHT_KEY);
        success();

        testing("both hashes wrong");
        data = new HttpRconData(WRONG_HASH_HEX, WRONG_HASH_HEX, RIGHT_CONTENT).decodeHashes();
        assert !data.verify(RIGHT_KEY);
        success();

        allSuccess();
    }
}
