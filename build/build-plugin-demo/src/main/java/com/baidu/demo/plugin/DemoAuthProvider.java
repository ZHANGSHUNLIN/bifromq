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

package com.baidu.demo.plugin;

import com.baidu.bifromq.plugin.authprovider.IAuthProvider;
import com.baidu.bifromq.plugin.authprovider.type.MQTT3AuthData;
import com.baidu.bifromq.plugin.authprovider.type.MQTT3AuthResult;
import com.baidu.bifromq.plugin.authprovider.type.MQTTAction;
import com.baidu.bifromq.plugin.authprovider.type.Reject;
import com.baidu.bifromq.type.ClientInfo;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;

@Slf4j
@Extension
public class DemoAuthProvider implements IAuthProvider {
    private static final String PLUGIN_AUTHPROVIDER_URL = "plugin.authprovider.url";
    private final IAuthProvider delegate;

    public DemoAuthProvider() {
        IAuthProvider delegate1;
        String webhookUrl = System.getProperty(PLUGIN_AUTHPROVIDER_URL);
        if (webhookUrl == null) {
            log.info("No webhook url specified, the fallback behavior will reject all auth/check requests.");
            delegate1 = new FallbackAuthProvider();
        } else {
            try {
                URI webhookURI = URI.create(webhookUrl);
                delegate1 = new WebHookBasedAuthProvider(webhookURI);
                log.info("DemoAuthProvider's webhook URL: {}", webhookUrl);
            } catch (Throwable e) {
                delegate1 = new FallbackAuthProvider();
            }
        }
        delegate = delegate1;
    }

    @Override
    public CompletableFuture<MQTT3AuthResult> auth(MQTT3AuthData authData) {
        return delegate.auth(authData);
    }

    @Override
    public CompletableFuture<Boolean> check(ClientInfo client, MQTTAction action) {
        return delegate.check(client, action);
    }

    static class FallbackAuthProvider implements IAuthProvider {
        @Override
        public CompletableFuture<MQTT3AuthResult> auth(MQTT3AuthData authData) {
            return CompletableFuture.completedFuture(
                MQTT3AuthResult.newBuilder().setReject(Reject.newBuilder()
                        .setCode(Reject.Code.Error)
                        .setReason("No webhook url for auth provider configured")
                        .build())
                    .build());
        }

        @Override
        public CompletableFuture<Boolean> check(ClientInfo client, MQTTAction action) {
            return CompletableFuture.completedFuture(false);
        }
    }
}
