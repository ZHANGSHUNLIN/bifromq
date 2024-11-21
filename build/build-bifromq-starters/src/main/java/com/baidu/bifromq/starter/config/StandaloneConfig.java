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

package com.baidu.bifromq.starter.config;

import com.baidu.bifromq.baseenv.EnvProvider;
import com.baidu.bifromq.starter.config.model.ClusterConfig;
import com.baidu.bifromq.starter.config.model.RPCConfig;
import com.baidu.bifromq.starter.config.model.api.APIServerConfig;
import com.baidu.bifromq.starter.config.model.dict.SessionDictServiceConfig;
import com.baidu.bifromq.starter.config.model.dist.DistServiceConfig;
import com.baidu.bifromq.starter.config.model.inbox.InboxServiceConfig;
import com.baidu.bifromq.starter.config.model.mqtt.MQTTServiceConfig;
import com.baidu.bifromq.starter.config.model.retain.RetainServiceConfig;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StandaloneConfig {
    private String authProviderFQN = null;
    private String resourceThrottlerFQN = null;
    private String settingProviderFQN = null;

    private int bgTaskThreads = Math.max(1, EnvProvider.INSTANCE.availableProcessors() / 4);

    @JsonSetter(nulls = Nulls.SKIP)
    private ClusterConfig clusterConfig = new ClusterConfig();

    @JsonSetter(nulls = Nulls.SKIP)
    private RPCConfig rpcConfig = new RPCConfig();

    @JsonSetter(nulls = Nulls.SKIP)
    private MQTTServiceConfig mqttServiceConfig = new MQTTServiceConfig();

    @JsonSetter(nulls = Nulls.SKIP)
    private DistServiceConfig distServiceConfig = new DistServiceConfig();

    @JsonSetter(nulls = Nulls.SKIP)
    private InboxServiceConfig inboxServiceConfig = new InboxServiceConfig();

    @JsonSetter(nulls = Nulls.SKIP)
    private RetainServiceConfig retainServiceConfig = new RetainServiceConfig();

    @JsonSetter(nulls = Nulls.SKIP)
    private SessionDictServiceConfig sessionDictServiceConfig = new SessionDictServiceConfig();

    @JsonSetter(nulls = Nulls.SKIP)
    private APIServerConfig apiServerConfig = new APIServerConfig();

}
