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

package com.baidu.bifromq.retain.server.scheduler;

import static com.baidu.bifromq.retain.store.schema.KVSchemaUtil.retainMessageKey;

import com.baidu.bifromq.basekv.client.IBaseKVStoreClient;
import com.baidu.bifromq.basekv.client.scheduler.MutationCallScheduler;
import com.baidu.bifromq.retain.rpc.proto.RetainReply;
import com.baidu.bifromq.retain.rpc.proto.RetainRequest;
import com.baidu.bifromq.sysprops.props.DataPlaneMaxBurstLatencyMillis;
import com.google.protobuf.ByteString;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetainCallScheduler extends MutationCallScheduler<RetainRequest, RetainReply, BatchRetainCall>
    implements IRetainCallScheduler {

    public RetainCallScheduler(IBaseKVStoreClient retainStoreClient) {
        super(BatchRetainCall::new, Duration.ofMillis(DataPlaneMaxBurstLatencyMillis.INSTANCE.get()).toNanos(),
            retainStoreClient);
    }

    @Override
    protected ByteString rangeKey(RetainRequest request) {
        return retainMessageKey(request.getPublisher().getTenantId(), request.getTopic());
    }
}
