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

package com.baidu.bifromq.mqtt.handler;

import static com.baidu.bifromq.plugin.settingprovider.Setting.DebugModeEnabled;
import static com.baidu.bifromq.plugin.settingprovider.Setting.ForceTransient;
import static com.baidu.bifromq.plugin.settingprovider.Setting.InBoundBandWidth;
import static com.baidu.bifromq.plugin.settingprovider.Setting.MaxTopicAlias;
import static com.baidu.bifromq.plugin.settingprovider.Setting.MaxTopicFiltersPerSub;
import static com.baidu.bifromq.plugin.settingprovider.Setting.MaxTopicLength;
import static com.baidu.bifromq.plugin.settingprovider.Setting.MaxTopicLevelLength;
import static com.baidu.bifromq.plugin.settingprovider.Setting.MaxTopicLevels;
import static com.baidu.bifromq.plugin.settingprovider.Setting.MaxUserPayloadBytes;
import static com.baidu.bifromq.plugin.settingprovider.Setting.MaximumQoS;
import static com.baidu.bifromq.plugin.settingprovider.Setting.MsgPubPerSec;
import static com.baidu.bifromq.plugin.settingprovider.Setting.OfflineExpireTimeSeconds;
import static com.baidu.bifromq.plugin.settingprovider.Setting.OfflineOverflowDropOldest;
import static com.baidu.bifromq.plugin.settingprovider.Setting.OfflineQueueSize;
import static com.baidu.bifromq.plugin.settingprovider.Setting.OutBoundBandWidth;
import static com.baidu.bifromq.plugin.settingprovider.Setting.ReceivingMaximum;
import static com.baidu.bifromq.plugin.settingprovider.Setting.RetainEnabled;
import static com.baidu.bifromq.plugin.settingprovider.Setting.RetainMessageMatchLimit;
import static com.baidu.bifromq.plugin.settingprovider.Setting.SharedSubscriptionEnabled;
import static com.baidu.bifromq.plugin.settingprovider.Setting.SubscriptionIdentifierEnabled;
import static com.baidu.bifromq.plugin.settingprovider.Setting.WildcardSubscriptionEnabled;

import com.baidu.bifromq.plugin.settingprovider.ISettingProvider;
import com.baidu.bifromq.type.QoS;

public class TenantSettings {
    public final boolean debugMode;
    public final boolean forceTransient;
    public final boolean retainEnabled;
    public final boolean wildcardSubscriptionEnabled;
    public final boolean subscriptionIdentifierEnabled;
    public final boolean sharedSubscriptionEnabled;
    public final QoS maxQoS;
    public final int maxSEI;
    public final int maxTopicLevelLength;
    public final int maxTopicLevels;
    public final int maxTopicLength;
    public final int maxPacketSize;
    public final int maxTopicAlias;
    public final long inboundBandwidth;
    public final long outboundBandwidth;
    public final int receiveMaximum;
    public final int maxMsgPerSec;
    public final int maxTopicFiltersPerSub;
    public final int inboxQueueLength;
    public final boolean inboxDropOldest;
    public final int retainMatchLimit;

    public TenantSettings(String tenantId, ISettingProvider provider) {
        debugMode = provider.provide(DebugModeEnabled, tenantId);
        forceTransient = provider.provide(ForceTransient, tenantId);
        retainEnabled = provider.provide(RetainEnabled, tenantId);
        wildcardSubscriptionEnabled = provider.provide(WildcardSubscriptionEnabled, tenantId);
        subscriptionIdentifierEnabled = provider.provide(SubscriptionIdentifierEnabled, tenantId);
        sharedSubscriptionEnabled = provider.provide(SharedSubscriptionEnabled, tenantId);
        maxQoS = QoS.forNumber(provider.provide(MaximumQoS, tenantId));
        maxSEI = provider.provide(OfflineExpireTimeSeconds, tenantId);
        maxTopicLevelLength = provider.provide(MaxTopicLevelLength, tenantId);
        maxTopicLevels = provider.provide(MaxTopicLevels, tenantId);
        maxTopicLength = provider.provide(MaxTopicLength, tenantId);
        maxPacketSize = provider.provide(MaxUserPayloadBytes, tenantId);
        maxTopicAlias = provider.provide(MaxTopicAlias, tenantId);
        inboundBandwidth = provider.provide(InBoundBandWidth, tenantId);
        outboundBandwidth = provider.provide(OutBoundBandWidth, tenantId);
        maxMsgPerSec = provider.provide(MsgPubPerSec, tenantId);
        receiveMaximum = provider.provide(ReceivingMaximum, tenantId);
        maxTopicFiltersPerSub = provider.provide(MaxTopicFiltersPerSub, tenantId);
        inboxQueueLength = provider.provide(OfflineQueueSize, tenantId);
        inboxDropOldest = provider.provide(OfflineOverflowDropOldest, tenantId);
        retainMatchLimit = provider.provide(RetainMessageMatchLimit, tenantId);
    }
}
