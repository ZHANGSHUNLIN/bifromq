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

package com.baidu.bifromq.mqtt.handler.v3;

import static com.baidu.bifromq.plugin.eventcollector.EventType.CLIENT_CONNECTED;
import static com.baidu.bifromq.plugin.eventcollector.EventType.KICKED;
import static com.baidu.bifromq.plugin.eventcollector.EventType.MQTT_SESSION_START;
import static com.baidu.bifromq.plugin.eventcollector.EventType.MQTT_SESSION_STOP;
import static io.netty.handler.codec.mqtt.MqttConnectReturnCode.CONNECTION_ACCEPTED;

import com.baidu.bifromq.inbox.rpc.proto.DetachReply;
import com.baidu.bifromq.mqtt.utils.MQTTMessageUtils;
import com.baidu.bifromq.sessiondict.rpc.proto.ServerRedirection;
import com.baidu.bifromq.type.ClientInfo;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MQTTKickTest extends BaseMQTTTest {

    @Test
    public void testKick() {
        mockAuthPass();
        mockSessionReg();
        mockInboxDetach(DetachReply.Code.NO_INBOX);
        MqttConnectMessage connectMessage = MQTTMessageUtils.mqttConnectMessage(true);
        channel.writeInbound(connectMessage);
        MqttConnAckMessage ackMessage = channel.readOutbound();
        Assert.assertEquals(CONNECTION_ACCEPTED, ackMessage.variableHeader().connectReturnCode());

        // kick

        onKill.get().onKill(ClientInfo.newBuilder().build(),
            ServerRedirection.newBuilder().setType(ServerRedirection.Type.NO_MOVE).build());

        channel.runPendingTasks();
        Assert.assertFalse(channel.isActive());
        verifyEvent(MQTT_SESSION_START, CLIENT_CONNECTED, KICKED, MQTT_SESSION_STOP);
    }
}
