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

package com.baidu.bifromq.dist.server;

import com.baidu.bifromq.baserpc.IRPCServer;
import com.baidu.bifromq.dist.RPCBluePrint;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
final class StandaloneDistServer extends AbstractDistServer {
    private final IRPCServer rpcServer;

    StandaloneDistServer(StandaloneDistServerBuilder builder) {
        super(builder);
        this.rpcServer = IRPCServer.newBuilder()
            .executor(builder.executor)
            .bindService(distService.bindService(), RPCBluePrint.INSTANCE)
            .host(builder.host)
            .port(builder.port)
            .bossEventLoopGroup(builder.bossEventLoopGroup)
            .workerEventLoopGroup(builder.workerEventLoopGroup)
            .crdtService(builder.crdtService)
            .sslContext(builder.sslContext)
            .build();
    }

    @Override
    public void start() {
        log.info("Starting dist server");
        log.debug("Starting rpc server");
        rpcServer.start();
        log.info("Dist Server started");
    }

    @SneakyThrows
    @Override
    public void shutdown() {
        log.info("Stopping dist server");
        log.debug("Stop dist rpc server");
        rpcServer.shutdown();
        super.shutdown();
        log.info("Dist server stopped");
    }
}
