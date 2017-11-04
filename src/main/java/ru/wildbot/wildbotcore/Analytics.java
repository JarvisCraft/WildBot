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

package ru.wildbot.wildbotcore;

import lombok.Synchronized;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.time.Instant;

public final class Analytics {
    private Instant beginTime = Instant.now();

    @Synchronized public void updateBeginTime() {
        beginTime = Instant.now();
    }

    @Synchronized public long getUptime() {
        return Duration.between(beginTime, Instant.now()).toMillis();
    }

    @Synchronized public String getUptimeFormatted() {
        return DurationFormatUtils.formatDurationHMS(getUptime());
    }
}
