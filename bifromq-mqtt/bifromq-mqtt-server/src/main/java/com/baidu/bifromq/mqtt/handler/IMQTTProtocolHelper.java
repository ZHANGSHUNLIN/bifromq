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

package com.baidu.bifromq.mqtt.handler;

import com.baidu.bifromq.inbox.storage.proto.TopicFilterOption;
import com.baidu.bifromq.mqtt.handler.record.ProtocolResponse;
import com.baidu.bifromq.plugin.authprovider.type.CheckResult;
import com.baidu.bifromq.retain.rpc.proto.RetainReply;
import com.baidu.bifromq.type.ClientInfo;
import com.baidu.bifromq.type.Message;
import com.baidu.bifromq.type.UserProperties;
import com.bifromq.plugin.resourcethrottler.TenantResourceType;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

public interface IMQTTProtocolHelper {
    record SubTask(String topicFilter, TopicFilterOption option, UserProperties userProperties) {
    }

    record PubResult(com.baidu.bifromq.dist.client.PubResult distResult, RetainReply.Result retainResult) {
    }

    enum SubResult {
        OK,
        EXISTS,
        NO_INBOX,
        EXCEED_LIMIT,
        NOT_AUTHORIZED,
        TOPIC_FILTER_INVALID,
        WILDCARD_NOT_SUPPORTED,
        SHARED_SUBSCRIPTION_NOT_SUPPORTED,
        SUBSCRIPTION_IDENTIFIER_NOT_SUPPORTED,
        BACK_PRESSURE_REJECTED,

        ERROR;
    }

    enum UnsubResult {
        OK,
        NO_SUB,
        NO_INBOX,
        NOT_AUTHORIZED,
        TOPIC_FILTER_INVALID,
        BACK_PRESSURE_REJECTED,
        ERROR;
    }

    UserProperties getUserProps(MqttPublishMessage mqttMessage);

    UserProperties getUserProps(MqttUnsubscribeMessage mqttMessage);

    boolean checkPacketIdUsage();

    ProtocolResponse onInboxTransientError();

    Optional<Integer> sessionExpiryIntervalOnDisconnect(MqttMessage disconnectMessage);

    ProtocolResponse onDisconnect();

    ProtocolResponse onResourceExhaustedDisconnect(TenantResourceType resourceType);

    ProtocolResponse respondDisconnectProtocolError();

    boolean isNormalDisconnect(MqttMessage message);

    boolean isDisconnectWithLWT(MqttMessage message);

    ProtocolResponse respondDecodeError(MqttMessage message);

    ProtocolResponse respondDuplicateConnect(MqttConnectMessage message);

    ProtocolResponse validateSubMessage(MqttSubscribeMessage message);

    List<SubTask> getSubTask(MqttSubscribeMessage message);

    ProtocolResponse onSubBackPressured(MqttSubscribeMessage subMessage);

    ProtocolResponse buildSubAckMessage(MqttSubscribeMessage subMessage, List<SubResult> results);

    MqttSubAckMessage respondPacketIdInUse(MqttSubscribeMessage message);

    ProtocolResponse validateUnsubMessage(MqttUnsubscribeMessage message);

    MqttUnsubAckMessage respondPacketIdInUse(MqttUnsubscribeMessage message);

    ProtocolResponse onUnsubBackPressured(MqttUnsubscribeMessage unsubMessage);

    ProtocolResponse buildUnsubAckMessage(MqttUnsubscribeMessage unsubMessage, List<UnsubResult> results);

    MqttMessage onPubRelReceived(MqttMessage message, boolean packetIdFound);

    boolean isQoS2Received(MqttMessage message);

    ProtocolResponse respondPubRecMsg(MqttMessage message, boolean packetIdNotFound);

    int clientReceiveMaximum();

    ProtocolResponse onKick(ClientInfo killer);

    ProtocolResponse onRedirect(boolean isPermanent, @Nullable String serverReference);

    MqttPublishMessage buildMqttPubMessage(int packetId, MQTTSessionHandler.SubMessage message, boolean isDup);

    ProtocolResponse respondReceivingMaximumExceeded(MqttPublishMessage mqttMessage);

    ProtocolResponse respondPubRateExceeded(MqttPublishMessage mqttMessage);

    ProtocolResponse validatePubMessage(MqttPublishMessage message);

    String getTopic(MqttPublishMessage message);

    Message buildDistMessage(MqttPublishMessage message);

    ProtocolResponse onQoS0DistDenied(String topic, Message distMessage, CheckResult result);

    ProtocolResponse onQoS0PubHandled(PubResult result, MqttPublishMessage message, UserProperties userProps);

    ProtocolResponse onQoS1DistDenied(String topic, int packetId, Message distMessage, CheckResult result);

    ProtocolResponse onQoS1PubHandled(PubResult result, MqttPublishMessage message, UserProperties userProps);

    ProtocolResponse respondQoS2PacketInUse(MqttPublishMessage message);

    ProtocolResponse onQoS2DistDenied(String topic, int packetId, Message distMessage, CheckResult result);

    ProtocolResponse onQoS2PubHandled(PubResult result, MqttPublishMessage message, UserProperties userProps);

    ProtocolResponse onIdleTimeout(int keepAliveTimeSeconds);
}
