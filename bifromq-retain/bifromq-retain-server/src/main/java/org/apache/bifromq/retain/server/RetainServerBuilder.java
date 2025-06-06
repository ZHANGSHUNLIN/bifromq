/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.bifromq.retain.server;

import org.apache.bifromq.basekv.client.IBaseKVStoreClient;
import org.apache.bifromq.baserpc.server.RPCServerBuilder;
import org.apache.bifromq.dist.client.IDistClient;
import org.apache.bifromq.plugin.settingprovider.ISettingProvider;
import org.apache.bifromq.plugin.subbroker.ISubBrokerManager;
import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The builder for building Retain Server.
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Accessors(fluent = true)
@Setter
public class RetainServerBuilder {
    RPCServerBuilder rpcServerBuilder;
    ISettingProvider settingProvider;
    ISubBrokerManager subBrokerManager;
    IDistClient distClient;
    IBaseKVStoreClient retainStoreClient;
    Map<String, String> attributes = new HashMap<>();
    Set<String> defaultGroupTags = new HashSet<>();
    int workerThreads = 0;

    public IRetainServer build() {
        Preconditions.checkNotNull(rpcServerBuilder, "RPC Server Builder is null");
        return new RetainServer(this);
    }
}
