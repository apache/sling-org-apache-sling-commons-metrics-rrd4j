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
package org.apache.sling.commons.metrics.rrd4j.impl;

import java.io.File;
import java.io.IOException;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RRD4JReporterTest {

    private static final File RRD = new File(new File("target", "metrics"), "metrics.rrd");

    private MetricRegistry registry = new MetricRegistry();
    private RRD4JReporter reporter;

    @Before
    public void before() throws IOException {
        RRD.delete();
        registry.register("myMetric", new TestGauge(42));
        reporter = RRD4JReporter.forRegistry(registry)
                .withPath(RRD)
                .withArchives(new String[]{"RRA:AVERAGE:0.5:1:60"})
                .withDatasources(new String[]{"DS:sling_myMetric:GAUGE:300:0:U"})
                .withStep(1)
                .build();
    }

    @After
    public void after() {
        reporter.close();
        RRD.delete();
    }

    @Test
    public void tooFrequentSamples() {
        reporter.report();
        reporter.report();
    }

    private static final class TestGauge implements Gauge<Long> {

        private final long value;

        TestGauge(long value) {
            this.value = value;
        }

        @Override
        public Long getValue() {
            return value;
        }
    }
}

