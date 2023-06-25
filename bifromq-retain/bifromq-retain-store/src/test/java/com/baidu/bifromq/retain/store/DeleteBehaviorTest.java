/*
 * Copyright (c) 2023. Baidu, Inc. All Rights Reserved.
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

package com.baidu.bifromq.retain.store;

import static org.testng.AssertJUnit.assertEquals;

import com.baidu.bifromq.retain.rpc.proto.MatchCoProcReply;
import com.baidu.bifromq.retain.rpc.proto.RetainCoProcReply;
import org.testng.annotations.Test;

public class DeleteBehaviorTest extends RetainStoreTest {
    @Test(groups = "integration")
    public void deleteFromEmptyRetainSet() {
        String trafficId = "trafficId";
        String topic = "/a/b/c";
        // empty payload signal deletion
        RetainCoProcReply reply = requestRetain(trafficId, 10, message(topic, ""));
        assertEquals(RetainCoProcReply.Result.CLEARED, reply.getResult());
    }

    @Test(groups = "integration")
    public void deleteNonExisting() {
        String trafficId = "trafficId";
        // empty payload signal deletion
        assertEquals(RetainCoProcReply.Result.RETAINED,
            requestRetain(trafficId, 10, message("/a/b/c", "hello")).getResult());

        assertEquals(RetainCoProcReply.Result.CLEARED,
            requestRetain(trafficId, 10, message("/a", "")).getResult());
    }

    @Test(groups = "integration")
    public void deleteNonExpired() {
        String trafficId = "trafficId";
        String topic = "/a/b/c";
        // empty payload signal deletion
        assertEquals(RetainCoProcReply.Result.RETAINED,
            requestRetain(trafficId, 10, message(topic, "hello")).getResult());

        assertEquals(RetainCoProcReply.Result.CLEARED,
            requestRetain(trafficId, 10, message(topic, "")).getResult());

        MatchCoProcReply matchReply = requestMatch(trafficId, topic, 10);
        assertEquals(0, matchReply.getMessagesCount());
    }
}
