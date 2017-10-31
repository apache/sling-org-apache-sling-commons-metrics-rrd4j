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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.codahale.metrics.Clock;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rrd4j.core.RrdDb;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

public class RestartTest {

    private static final File RRD = new File(new File("target", "metrics"), "metrics.rrd");

    private MetricRegistry registry = new MetricRegistry();
    private RRD4JReporter reporter;
    private Counter counter = new Counter();
    private TestClock clock = new TestClock();

    @Before
    public void before() throws IOException {
        RRD.delete();
        clock.waitUntil(System.currentTimeMillis());
        registry.register("myCounter", counter);
        reporter = createReporter();
    }

    @After
    public void after() {
        reporter.close();
        RRD.delete();
    }

    @Test
    public void restart() throws Exception {
        restart(10);
    }

    @Test
    public void restartNoDelay() throws Exception {
        restart(0);
    }

    private void restart(int delaySecs) throws Exception {
        long start = clock.getTime() / 1000;
        // report some samples
        for (int i = 0; i < 3; i++) {
            counter.inc();
            wait(1, SECONDS);
            reporter.report();
        }
        // shut down
        reporter.close();
        // set count to zero
        counter.dec(counter.getCount());
        // restart after some delay
        wait(delaySecs, SECONDS);
        reporter = createReporter();
        // report some samples
        for (int i = 0; i < 3; i++) {
            counter.inc();
            wait(1, SECONDS);
            reporter.report();
        }
        // shut down
        reporter.close();
        long end = clock.getTime() / 1000;
        // check DB
        RrdDb db = new RrdDb(RRD.getPath());
        try {
            double[] values = db.createFetchRequest(
                    db.getArchive(0).getConsolFun(), start, end)
                        .fetchData().getValues("0");
            for (double v : values) {
                if (Double.isNaN(v)) {
                    continue;
                }
                long longValue = (long) v;
                assertTrue(longValue + " > 1", longValue <= 1L);
            }
        } finally {
            db.close();
        }
    }

    private void wait(long duration, TimeUnit unit) {
        clock.waitUntil(clock.getTime() + unit.toMillis(duration));
    }

    private RRD4JReporter createReporter() throws IOException {
        return RRD4JReporter.forRegistry(registry)
                .withPath(RRD)
                .withArchives(new String[]{"RRA:AVERAGE:0.5:1:60"})
                .withDatasources(new String[]{"DS:myCounter:COUNTER:300:0:U"})
                .withStep(1)
                .withClock(clock)
                .build();
    }

    private static class TestClock extends Clock.UserTimeClock {

        private AtomicLong time = new AtomicLong();

        @Override
        public long getTime() {
            return time.get();
        }

        void waitUntil(long time) {
            while (this.time.get() < time) {
                this.time.compareAndSet(this.time.get(), time);
            }
        }
    }
}
