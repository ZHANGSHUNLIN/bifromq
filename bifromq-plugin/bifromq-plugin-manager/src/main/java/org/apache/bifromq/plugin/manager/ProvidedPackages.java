/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.bifromq.plugin.manager;

import java.util.HashSet;
import java.util.Set;

public class ProvidedPackages {
    private static final Set<String> PACKAGES = new HashSet<>();

    static {
        PACKAGES.add("org.apache.bifromq.type.");
        PACKAGES.add("org.apache.bifromq.plugin.");
        PACKAGES.add("io.micrometer.core.");
        PACKAGES.add("com.google.protobuf.");
    }

    public static boolean isProvided(String className) {
        return PACKAGES.stream().anyMatch(className::startsWith);
    }
}
