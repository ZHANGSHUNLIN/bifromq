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

import static com.baidu.bifromq.metrics.TenantMetric.MqttPersistentFanOutBytes;
import static com.baidu.bifromq.plugin.eventcollector.ThreadLocalEventPool.getLocal;
import static com.bifromq.plugin.resourcethrottler.TenantResourceType.TotalPersistentFanOutBytesPerSeconds;

import com.baidu.bifromq.deliverer.IMessageDeliverer;
import com.baidu.bifromq.deliverer.TopicMessagePackHolder;
import com.baidu.bifromq.dist.worker.schema.GroupMatching;
import com.baidu.bifromq.dist.worker.schema.Matching;
import com.baidu.bifromq.dist.worker.schema.NormalMatching;
import com.baidu.bifromq.metrics.ITenantMeter;
import com.baidu.bifromq.plugin.eventcollector.IEventCollector;
import com.baidu.bifromq.plugin.eventcollector.OutOfTenantResource;
import com.baidu.bifromq.sysprops.props.DistInlineFanOutThreshold;
import com.baidu.bifromq.sysprops.props.DistTopicMatchExpirySeconds;
import com.baidu.bifromq.type.ClientInfo;
import com.baidu.bifromq.type.RouteMatcher;
import com.baidu.bifromq.type.TopicMessagePack;
import com.baidu.bifromq.util.SizeUtil;
import com.bifromq.plugin.resourcethrottler.IResourceThrottler;
import com.bifromq.plugin.resourcethrottler.TenantResourceType;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.google.common.base.Charsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class DeliverExecutorGroup implements IDeliverExecutorGroup {
    // OuterCacheKey: OrderedSharedMatchingKey(<tenantId>, <escapedTopicFilter>)
    // InnerCacheKey: ClientInfo(<tenantId>, <type>, <metadata>)
    private final LoadingCache<OrderedSharedMatchingKey, Cache<ClientInfo, NormalMatching>> orderedSharedMatching;
    private final int inlineFanOutThreshold = DistInlineFanOutThreshold.INSTANCE.get();
    private final IEventCollector eventCollector;
    private final IResourceThrottler resourceThrottler;
    private final DeliverExecutor[] fanoutExecutors;

    DeliverExecutorGroup(IMessageDeliverer deliverer,
                         IEventCollector eventCollector,
                         IResourceThrottler resourceThrottler,
                         int groupSize) {
        int expirySec = DistTopicMatchExpirySeconds.INSTANCE.get();
        this.eventCollector = eventCollector;
        this.resourceThrottler = resourceThrottler;
        orderedSharedMatching = Caffeine.newBuilder()
            .expireAfterAccess(expirySec * 2L, TimeUnit.SECONDS)
            .scheduler(Scheduler.systemScheduler())
            .removalListener((RemovalListener<OrderedSharedMatchingKey, Cache<ClientInfo, NormalMatching>>)
                (key, value, cause) -> {
                    if (value != null) {
                        value.invalidateAll();
                    }
                })
            .build(k -> Caffeine.newBuilder()
                .expireAfterAccess(expirySec, TimeUnit.SECONDS)
                .build());
        fanoutExecutors = new DeliverExecutor[groupSize];
        for (int i = 0; i < groupSize; i++) {
            fanoutExecutors[i] = new DeliverExecutor(i, deliverer, eventCollector);
        }
    }

    @Override
    public void shutdown() {
        for (DeliverExecutor fanoutExecutor : fanoutExecutors) {
            fanoutExecutor.shutdown();
        }
        orderedSharedMatching.invalidateAll();
    }

    @Override
    public void submit(String tenantId, Set<Matching> routes, TopicMessagePack msgPack) {
        int msgPackSize = SizeUtil.estSizeOf(msgPack);
        TopicMessagePackHolder messagePackHolder = TopicMessagePackHolder.hold(msgPack);
        if (routes.size() == 1) {
            Matching matching = routes.iterator().next();
            prepareSend(matching, messagePackHolder, true);
            if (isSendToInbox(matching)) {
                ITenantMeter.get(matching.tenantId()).recordSummary(MqttPersistentFanOutBytes, msgPackSize);
            }
        } else if (routes.size() > 1) {
            boolean inline = routes.size() > inlineFanOutThreshold;
            boolean hasTFanOutBandwidth =
                resourceThrottler.hasResource(tenantId, TenantResourceType.TotalTransientFanOutBytesPerSeconds);
            boolean hasTFannedOutUnderThrottled = false;
            boolean hasPFanOutBandwidth =
                resourceThrottler.hasResource(tenantId, TenantResourceType.TotalPersistentFanOutBytesPerSeconds);
            boolean hasPFannedOutUnderThrottled = false;
            // we meter persistent fanout bytes here, since for transient fanout is actually happened in the broker
            long pFanoutBytes = 0;
            for (Matching matching : routes) {
                if (isSendToInbox(matching)) {
                    if (hasPFanOutBandwidth || !hasPFannedOutUnderThrottled) {
                        pFanoutBytes += msgPackSize;
                        prepareSend(matching, messagePackHolder, inline);
                        if (!hasPFanOutBandwidth) {
                            hasPFannedOutUnderThrottled = true;
                            for (TopicMessagePack.PublisherPack publisherPack : msgPack.getMessageList()) {
                                eventCollector.report(getLocal(OutOfTenantResource.class)
                                    .reason(TotalPersistentFanOutBytesPerSeconds.name())
                                    .clientInfo(publisherPack.getPublisher())
                                );
                            }
                        }
                    }
                } else if (hasTFanOutBandwidth || !hasTFannedOutUnderThrottled) {
                    prepareSend(matching, messagePackHolder, inline);
                    if (!hasTFanOutBandwidth) {
                        hasTFannedOutUnderThrottled = true;
                        for (TopicMessagePack.PublisherPack publisherPack : msgPack.getMessageList()) {
                            eventCollector.report(getLocal(OutOfTenantResource.class)
                                .reason(TenantResourceType.TotalTransientFanOutBytesPerSeconds.name())
                                .clientInfo(publisherPack.getPublisher())
                            );
                        }
                    }
                }
                if (hasPFannedOutUnderThrottled && hasTFannedOutUnderThrottled) {
                    break;
                }
            }
            ITenantMeter.get(tenantId).recordSummary(MqttPersistentFanOutBytes, pFanoutBytes);
        }
    }

    private boolean isSendToInbox(Matching matching) {
        return matching.type() == Matching.Type.Normal && ((NormalMatching) matching).subBrokerId() == 1;
    }

    @Override
    public void refreshOrderedShareSubRoutes(String tenantId, RouteMatcher routeMatcher) {
        if (log.isDebugEnabled()) {
            log.debug("Refresh ordered shared sub routes: tenantId={}, sharedTopicFilter={}", tenantId, routeMatcher);
        }
        orderedSharedMatching.invalidate(
            new OrderedSharedMatchingKey(tenantId, routeMatcher.getFilterLevelList()));
    }

    private void prepareSend(Matching matching, TopicMessagePackHolder msgPackHolder, boolean inline) {
        switch (matching.type()) {
            case Normal -> send((NormalMatching) matching, msgPackHolder, inline);
            case Group -> {
                GroupMatching groupMatching = (GroupMatching) matching;
                if (!groupMatching.ordered) {
                    // pick one route randomly
                    send(groupMatching.receiverList.get(
                        ThreadLocalRandom.current().nextInt(groupMatching.receiverList.size())), msgPackHolder, inline);
                } else {
                    // ordered shared subscription
                    Map<NormalMatching, TopicMessagePack.Builder> orderedRoutes = new HashMap<>();
                    for (TopicMessagePack.PublisherPack publisherPack : msgPackHolder.messagePack.getMessageList()) {
                        ClientInfo sender = publisherPack.getPublisher();
                        NormalMatching matchedInbox = orderedSharedMatching
                            .get(new OrderedSharedMatchingKey(groupMatching.tenantId(),
                                groupMatching.matcher.getFilterLevelList()))
                            .get(sender, senderInfo -> {
                                RendezvousHash<ClientInfo, NormalMatching> hash =
                                    RendezvousHash.<ClientInfo, NormalMatching>builder()
                                        .keyFunnel((from, into) -> into.putInt(from.hashCode()))
                                        .nodeFunnel((from, into) -> into.putString(from.receiverUrl(), Charsets.UTF_8))
                                        .nodes(groupMatching.receiverList)
                                        .build();
                                NormalMatching matchRecord = hash.get(senderInfo);
                                log.debug(
                                    "Ordered shared matching: sender={}: topicFilter={}, receiverId={}, subBroker={}",
                                    senderInfo,
                                    matchRecord.mqttTopicFilter(),
                                    matchRecord.matchInfo().getReceiverId(),
                                    matchRecord.subBrokerId());
                                return matchRecord;
                            });
                        // ordered share sub
                        orderedRoutes.computeIfAbsent(matchedInbox, k -> TopicMessagePack.newBuilder())
                            .setTopic(msgPackHolder.messagePack.getTopic())
                            .addMessage(publisherPack);
                    }
                    orderedRoutes.forEach((route, msgPackBuilder) ->
                        send(route, TopicMessagePackHolder.hold(msgPackBuilder.build()), inline));
                }
            }
        }
    }

    private void send(NormalMatching route, TopicMessagePackHolder msgPackHolder, boolean inline) {
        int idx = route.hashCode() % fanoutExecutors.length;
        if (idx < 0) {
            idx += fanoutExecutors.length;
        }
        fanoutExecutors[idx].submit(route, msgPackHolder, inline);
    }

    private record OrderedSharedMatchingKey(String tenantId, List<String> filterLevels) {
    }
}
