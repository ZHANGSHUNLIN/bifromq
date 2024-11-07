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

package com.baidu.bifromq.dist.worker;

import com.baidu.bifromq.basecluster.IAgentHost;
import com.baidu.bifromq.basekv.IBaseKVMetaService;
import com.baidu.bifromq.basekv.client.IBaseKVStoreClient;
import com.baidu.bifromq.basekv.store.option.KVRangeStoreOptions;
import com.baidu.bifromq.dist.client.IDistClient;
import com.baidu.bifromq.plugin.eventcollector.IEventCollector;
import com.baidu.bifromq.plugin.subbroker.ISubBrokerManager;
import com.bifromq.plugin.resourcethrottler.IResourceThrottler;
import com.google.protobuf.Struct;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

abstract class AbstractDistWorkerBuilder<T extends AbstractDistWorkerBuilder<T>>
    implements IDistWorkerBuilder {
    String clusterId = IDistWorker.CLUSTER_NAME;
    boolean bootstrap;
    IAgentHost agentHost;
    IBaseKVMetaService metaService;
    Executor queryExecutor;
    int tickerThreads;
    ScheduledExecutorService bgTaskExecutor;
    IEventCollector eventCollector;
    IResourceThrottler resourceThrottler;
    IDistClient distClient;
    IBaseKVStoreClient storeClient;
    ISubBrokerManager subBrokerManager;
    KVRangeStoreOptions storeOptions;
    Duration balancerRetryDelay = Duration.ofSeconds(5);
    Map<String, Struct> balancerFactoryConfig = new HashMap<>();
    Duration loadEstimateWindow = Duration.ofSeconds(5);

    AbstractDistWorkerBuilder() {
    }

    @SuppressWarnings("unchecked")
    private T thisT() {
        return (T) this;
    }

    public T clusterId(String clusterId) {
        this.clusterId = clusterId;
        return thisT();
    }

    public T bootstrap(boolean bootstrap) {
        this.bootstrap = bootstrap;
        return thisT();
    }

    public T agentHost(IAgentHost agentHost) {
        this.agentHost = agentHost;
        return thisT();
    }

    public T metaService(IBaseKVMetaService metaService) {
        this.metaService = metaService;
        return thisT();
    }

    public T queryExecutor(Executor queryExecutor) {
        this.queryExecutor = queryExecutor;
        return thisT();
    }

    public T tickerThreads(int threads) {
        this.tickerThreads = threads;
        return thisT();
    }

    public T bgTaskExecutor(ScheduledExecutorService bgTaskExecutor) {
        this.bgTaskExecutor = bgTaskExecutor;
        return thisT();
    }

    public T eventCollector(IEventCollector eventCollector) {
        this.eventCollector = eventCollector;
        return thisT();
    }

    public T resourceThrottler(IResourceThrottler resourceThrottler) {
        this.resourceThrottler = resourceThrottler;
        return thisT();
    }

    public T distClient(IDistClient distClient) {
        this.distClient = distClient;
        return thisT();
    }

    public T storeClient(IBaseKVStoreClient storeClient) {
        this.storeClient = storeClient;
        return thisT();
    }

    public T subBrokerManager(ISubBrokerManager subBrokerManager) {
        this.subBrokerManager = subBrokerManager;
        return thisT();
    }

    public T storeOptions(KVRangeStoreOptions storeOptions) {
        this.storeOptions = storeOptions;
        return thisT();
    }

    public T balanceRetryDelay(Duration delay) {
        this.balancerRetryDelay = delay;
        return thisT();
    }

    public T balancerFactoryConfig(Map<String, Struct> config) {
        this.balancerFactoryConfig.putAll(config);
        return thisT();
    }

    public T loadEstimateWindow(Duration window) {
        this.loadEstimateWindow = window;
        return thisT();
    }
}
