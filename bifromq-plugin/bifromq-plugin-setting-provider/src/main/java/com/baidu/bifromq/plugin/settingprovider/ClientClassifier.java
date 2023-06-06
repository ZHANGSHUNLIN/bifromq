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

package com.baidu.bifromq.plugin.settingprovider;

import com.baidu.bifromq.type.ClientInfo;

public interface ClientClassifier {
    /**
     * Return a string representing the class of the provided class.
     * <p/>
     * Note: implementor should make sure the method is performant, otherwise it will slow down message delivery.
     *
     * @param clientInfo the client info
     * @return a string represent the class of the given client
     */
    String classify(ClientInfo clientInfo);
}
