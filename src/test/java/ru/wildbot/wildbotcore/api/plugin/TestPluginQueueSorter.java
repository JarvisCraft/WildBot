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

package ru.wildbot.wildbotcore.api.plugin;

import org.junit.Assert;
import org.junit.Test;
import ru.wildbot.wildbotcore.test.WildBotTest;

import java.util.HashSet;
import java.util.Set;

public class TestPluginQueueSorter extends WildBotTest {
    @Test
    public void testNull() {
        testing("PluginQueueSorter Given Null As `plugins`");
        final PluginQueueSorter sorter = new PluginQueueSorter(null);
        Set<JavaPluginInQueue> plugins = sorter.sort();

        Assert.assertNotNull(plugins);
        Assert.assertEquals(0, plugins.size());

        allSuccess();
    }

    @Test
    public void testNone() {
        testing("All Empty Plugins Set Sorting");

        Set<JavaPluginInQueue> plugins = new HashSet<>();
        final PluginQueueSorter sorter = new PluginQueueSorter(plugins);
        plugins = sorter.sort();

        Assert.assertNotNull(plugins);
        Assert.assertEquals(0, plugins.size());
        success();

        allSuccess();
    }
}
