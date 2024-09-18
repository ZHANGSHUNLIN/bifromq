/*
 * Copyright (c) 2023. The BifroMQ Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.baidu.bifromq.retain.utils;

import static com.baidu.bifromq.retain.utils.TopicUtil.match;
import static com.baidu.bifromq.util.TopicUtil.parse;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class TopicUtilTest {
    @Test
    public void matches() {
        assertTrue(matches("/", "/"));
        assertTrue(matches("/", "+/+"));
        assertTrue(matches("/", "+/#"));
        assertTrue(matches("/", "#"));
        assertTrue(matches("//", "//"));
        assertTrue(matches("//", "#"));
        assertTrue(matches("//", "+/#"));
        assertTrue(matches("a", "a"));
        assertTrue(matches("a", "#"));
        assertTrue(matches("a", "+"));
        assertTrue(matches("a/", "+/#"));
        assertTrue(matches("a/", "a/"));
        assertTrue(matches("/a", "/a"));
        assertTrue(matches("/a", "/a/#"));
        assertTrue(matches("/a", "/+"));
        assertTrue(matches("/a/b/c", "/+/#"));
        assertTrue(matches("/a/b/c", "/#"));
        assertTrue(matches("/a/b/c", "#"));
        assertTrue(matches("/a/b/c", "/a/b/c/#"));
        assertTrue(matches("/a/b/c", "/a/b/#"));

        assertFalse(matches("a", "/a"));
        assertFalse(matches("a", "+/"));
        assertFalse(matches("a/", "/a"));
    }

    private boolean matches(String topic, String topicFilter) {
        return match(parse(topic, false), parse(topicFilter, false));
    }
}
