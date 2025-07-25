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

package org.apache.bifromq.starter.config.model;

import static java.lang.Math.max;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.bifromq.baseenv.EnvProvider;
import org.rocksdb.util.SizeUnit;

@Getter
@Setter
@Accessors(chain = true)
public class RocksDBEngineConfig extends StorageEngineConfig {
    private String dataPathRoot = "";
    private boolean manualCompaction = false;
    private int compactMinTombstoneKeys = 200000;
    private int compactMinTombstoneRanges = 100000;
    private double compactTombstoneRatio = 0.3; // 30%
    private boolean asyncWALFlush = true; // only work for wal engine
    private boolean fsyncWAL = false; // only work for wal engine
    private long blockCacheSize = 32 * SizeUnit.MB;
    private long writeBufferSize = 128 * SizeUnit.MB;
    private int maxWriteBufferNumber = 6;
    private int minWriteBufferNumberToMerge = 2;
    private long minBlobSize = 2 * SizeUnit.KB;
    private int increaseParallelism = max(EnvProvider.INSTANCE.availableProcessors(), 2);
    private int maxBackgroundJobs = max(EnvProvider.INSTANCE.availableProcessors(), 2);
    private int level0FileNumCompactionTrigger = 8;
    private int level0SlowdownWritesTrigger = 20;
    private int level0StopWritesTrigger = 24;
    private long maxBytesForLevelBase = writeBufferSize * minWriteBufferNumberToMerge * level0FileNumCompactionTrigger;
    private long targetFileSizeBase = maxBytesForLevelBase / 10;
}
