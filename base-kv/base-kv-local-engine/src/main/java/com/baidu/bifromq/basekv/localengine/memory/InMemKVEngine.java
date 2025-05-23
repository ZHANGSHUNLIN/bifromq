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

package com.baidu.bifromq.basekv.localengine.memory;

import com.baidu.bifromq.basekv.localengine.AbstractKVEngine;
import java.util.UUID;

abstract class InMemKVEngine<E extends InMemKVEngine<E, T>, T extends InMemKVSpace<E, T>>
    extends AbstractKVEngine<T, InMemKVEngineConfigurator> {
    private final String identity;

    public InMemKVEngine(String overrideIdentity, InMemKVEngineConfigurator c) {
        super(overrideIdentity, c);
        if (overrideIdentity != null && !overrideIdentity.trim().isEmpty()) {
            identity = overrideIdentity;
        } else {
            identity = UUID.randomUUID().toString();
        }
    }

    @Override
    protected void doStart(String... metricTags) {

    }

    @Override
    protected void doStop() {

    }

    @Override
    public String id() {
        return identity;
    }
}
