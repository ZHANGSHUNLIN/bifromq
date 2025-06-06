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

package org.apache.bifromq.dist.worker.hinter;

import java.util.concurrent.atomic.AtomicInteger;

class RecordEstimation {
    private final AtomicInteger recordTotalSize = new AtomicInteger();
    private final AtomicInteger totalCount = new AtomicInteger();
    private final boolean isDelete;

    RecordEstimation(boolean isDelete) {
        this.isDelete = isDelete;
    }

    void addRecordSize(int recordSize) {
        recordTotalSize.addAndGet(recordSize);
        totalCount.incrementAndGet();
    }

    int avgRecordSize() {
        return recordTotalSize.get() / totalCount.get();
    }

    int tombstoneSize() {
        return isDelete ? recordTotalSize.get() : 0;
    }
}
