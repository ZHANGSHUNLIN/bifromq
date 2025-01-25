/*
 * Copyright (c) 2024. The BifroMQ Authors. All Rights Reserved.
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

package com.baidu.bifromq.baserpc.client;

import com.baidu.bifromq.baserpc.client.loadbalancer.IServerGroupRouter;
import com.baidu.bifromq.baserpc.client.loadbalancer.IServerSelector;
import java.util.Optional;

class DummyServerSelector implements IServerSelector {
    public static final IServerSelector INSTANCE = new DummyServerSelector();

    @Override
    public boolean exists(String serverId) {
        return false;
    }

    @Override
    public IServerGroupRouter get(String tenantId) {
        return new IServerGroupRouter() {
            @Override
            public boolean isSameGroup(IServerGroupRouter other) {
                return false;
            }

            @Override
            public Optional<String> random() {
                return Optional.empty();
            }

            @Override
            public Optional<String> roundRobin() {
                return Optional.empty();
            }

            @Override
            public Optional<String> tryRoundRobin() {
                return Optional.empty();
            }

            @Override
            public Optional<String> hashing(String key) {
                return Optional.empty();
            }
        };
    }

    @Override
    public String toString() {
        return "DummyServerSelector";
    }
}
