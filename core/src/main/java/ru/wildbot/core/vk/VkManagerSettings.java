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

package ru.wildbot.core.vk;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.wildbot.core.data.json.AbstractJsonData;

@NoArgsConstructor
@AllArgsConstructor
public class VkManagerSettings extends AbstractJsonData {
    @SerializedName("group-id") @Getter private int groupId = 123456789;
    @SerializedName("group-key") @Getter private String groupKey = "1234567890abcdefghABcdEFgh0192";
}
