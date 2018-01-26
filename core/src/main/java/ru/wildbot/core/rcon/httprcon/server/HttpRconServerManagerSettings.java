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

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.wildbot.core.data.json.AbstractJsonData;

@NoArgsConstructor
@AllArgsConstructor
public class HttpRconServerManagerSettings extends AbstractJsonData {
    @Getter private int port = 12524;
    @NonNull @Getter private String key = "MyS3cr37K$&";
    @SerializedName("log-unauthorized") @Getter private boolean logUnauthorised = true;
    @SerializedName("log-unverified") @Getter private boolean logUnverified = true;
    @SerializedName("verify-content") @Getter private boolean verifyContent = true;
    @SerializedName("log-malformed") @Getter private boolean logMalformed = true;
    @SerializedName("log-malformed-content") @Getter private boolean logMalformedContent = true;
    @SerializedName("log-received") @Getter private boolean logReceived = true;
    @SerializedName("log-received-content") @Getter private boolean logReceivedContent = true;
}
