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

package com.baidu.bifromq.inbox.store;

import com.baidu.bifromq.basekv.store.api.IKVReader;
import com.baidu.bifromq.inbox.storage.proto.InboxMetadata;
import com.baidu.bifromq.plugin.eventcollector.IEventCollector;
import lombok.SneakyThrows;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.baidu.bifromq.basekv.utils.BoundaryUtil.FULL_BOUNDARY;
import static com.baidu.bifromq.metrics.TenantMetric.MqttPersistentSessionNumGauge;
import static com.baidu.bifromq.metrics.TenantMetric.MqttPersistentSessionSpaceGauge;
import static com.baidu.bifromq.metrics.TenantMetric.MqttPersistentSubCountGauge;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TenantsStateTest extends MeterTest {
    @Mock
    private IEventCollector eventCollector;
    @Mock
    private IKVReader reader;
    private AutoCloseable closeable;

    @BeforeMethod
    public void setup() {
        super.setup();
        closeable = MockitoAnnotations.openMocks(this);
        when(reader.boundary()).thenReturn(FULL_BOUNDARY);
    }

    @SneakyThrows
    @AfterMethod
    public void tearDown() {
        closeable.close();
        super.tearDown();
    }

    @Test
    public void testGetEmpty() {
        TenantsState tenantsState = new TenantsState(eventCollector, reader);
        assertTrue(tenantsState.getAllTenantIds().isEmpty());
        assertTrue(tenantsState.getAll("tenantId").isEmpty());
        assertTrue(tenantsState.getAll("tenantId", "inboxId").isEmpty());
        assertTrue(tenantsState.get("tenantId", "inboxId", 0).isEmpty());
    }

    @Test
    public void testUpsert() {
        when(reader.size(any())).thenReturn(1L);
        String tenantId = "tenantId" + System.nanoTime();
        InboxMetadata inboxMetadata = InboxMetadata.newBuilder()
                .setInboxId("testInboxId")
                .setIncarnation(1)
                .build();
        TenantsState tenantsState = new TenantsState(eventCollector, reader);
        tenantsState.upsert(tenantId, inboxMetadata);
        assertGaugeValue(tenantId, MqttPersistentSubCountGauge, 0);
        assertGaugeValue(tenantId, MqttPersistentSessionNumGauge, 1);
        assertGaugeValue(tenantId, MqttPersistentSessionSpaceGauge, 1);

        assertEquals(tenantsState.getAllTenantIds().size(), 1);
        assertEquals(tenantsState.getAll(tenantId).size(), 1);
        assertEquals(tenantsState.getAll(tenantId, inboxMetadata.getInboxId()).size(), 1);
        assertTrue(tenantsState.get(tenantId, inboxMetadata.getInboxId(), inboxMetadata.getIncarnation()).isPresent());
    }

    @Test
    public void testRemove() {
        when(reader.size(any())).thenReturn(1L);
        String tenantId = "tenantId" + System.nanoTime();
        InboxMetadata inboxMetadata = InboxMetadata.newBuilder()
                .setInboxId("testInboxId")
                .setIncarnation(1)
                .build();
        InboxMetadata inboxMetadata1 = InboxMetadata.newBuilder()
                .setInboxId("testInboxId1")
                .setIncarnation(1)
                .build();
        TenantsState tenantsState = new TenantsState(eventCollector, reader);
        tenantsState.upsert(tenantId, inboxMetadata);
        tenantsState.upsert(tenantId, inboxMetadata1);
        tenantsState.remove(tenantId, inboxMetadata.getInboxId(), inboxMetadata.getIncarnation());
        assertGaugeValue(tenantId, MqttPersistentSubCountGauge, 0);
        assertGaugeValue(tenantId, MqttPersistentSessionNumGauge, 1);
        assertGaugeValue(tenantId, MqttPersistentSessionSpaceGauge, 1);

        tenantsState.remove(tenantId, inboxMetadata1.getInboxId(), inboxMetadata1.getIncarnation());
        assertNoGauge(tenantId, MqttPersistentSubCountGauge);
        assertNoGauge(tenantId, MqttPersistentSessionNumGauge);
        assertNoGauge(tenantId, MqttPersistentSessionSpaceGauge);
    }

    @Test
    public void testReset() {
        when(reader.size(any())).thenReturn(1L);
        String tenantId = "tenantId" + System.nanoTime();
        InboxMetadata inboxMetadata = InboxMetadata.newBuilder()
                .setInboxId("testInboxId")
                .setIncarnation(1)
                .build();
        InboxMetadata inboxMetadata1 = InboxMetadata.newBuilder()
                .setInboxId("testInboxId1")
                .setIncarnation(1)
                .build();
        TenantsState tenantsState = new TenantsState(eventCollector, reader);
        tenantsState.upsert(tenantId, inboxMetadata);
        tenantsState.upsert(tenantId, inboxMetadata1);

        tenantsState.reset();
        assertNoGauge(tenantId, MqttPersistentSubCountGauge);
        assertNoGauge(tenantId, MqttPersistentSessionNumGauge);
        assertNoGauge(tenantId, MqttPersistentSessionSpaceGauge);
    }
}
