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

package org.apache.bifromq.basekv.balance.command;

import org.apache.bifromq.basekv.utils.KVRangeIdUtil;
import com.google.protobuf.ByteString;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class SplitCommand extends RangeCommand {

    private ByteString splitKey;

    @Override
    public CommandType type() {
        return CommandType.SPLIT;
    }

    @Override
    public String toString() {
        return String.format("SplitCommand{toStore=%s, kvRangeId=%s, expectedVer=%s, splitKey=%s}",
            getToStore(), KVRangeIdUtil.toString(getKvRangeId()), printableVer(), splitKey.toStringUtf8());
    }

}
