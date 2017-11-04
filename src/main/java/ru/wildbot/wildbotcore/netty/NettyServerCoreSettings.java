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

package ru.wildbot.wildbotcore.netty;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.wildbot.wildbotcore.data.json.AbstractJsonData;

/**
 * Class representing all settings used with {@link NettyServerCore}
 */
@NoArgsConstructor
@AllArgsConstructor
public class NettyServerCoreSettings extends AbstractJsonData {
    @SerializedName("parent-threads") @Getter private int parentThreads = 0;
    @SerializedName("child-threads") @Getter private int childThreads = 0;
    @SerializedName("use-native") @Getter private boolean useNative = true;
    @SerializedName("log") @Getter private boolean log = true;
}
