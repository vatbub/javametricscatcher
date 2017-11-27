package com.github.vatbub.javametricscatcher.client;

/*-
 * #%L
 * javametricscatcher.client
 * %%
 * Copyright (C) 2017 Frederik Kammel
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import com.codahale.metrics.*;
import com.codahale.metrics.Timer;
import com.esotericsoftware.kryonet.Client;
import com.github.vatbub.javametricscatcher.common.KryoCommon;
import com.github.vatbub.javametricscatcher.common.MetricsUpdateRequest;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A reporter which outputs measurements to a {@link PrintStream}, like {@code System.out}.
 */
public class ServerReporter extends ScheduledReporter {
    /**
     * Returns a new {@link Builder} for {@link ServerReporter}.
     *
     * @param registry the registry to report
     * @return a {@link Builder} instance for a {@link ServerReporter}
     */
    public static Builder forRegistry(MetricRegistry registry, String serverHost) throws UnknownHostException {
        return new Builder(registry, serverHost);
    }

    /**
     * A builder for {@link ServerReporter} instances. Defaults to using the default locale and
     * time zone, writing to {@code System.out}, converting rates to events/second, converting
     * durations to milliseconds, and not filtering metrics.
     */
    public static class Builder {
        private final MetricRegistry registry;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;
        private ScheduledExecutorService executor;
        private boolean shutdownExecutorOnStop;
        private InetAddress host;
        private int tcpPort;
        private int udpPort;
        private boolean useUDP;
        private int timeout;

        private Builder(MetricRegistry registry, String serverHost) throws UnknownHostException {
            this.registry = registry;
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
            this.executor = null;
            this.shutdownExecutorOnStop = true;
            this.host = InetAddress.getByName(serverHost);
            this.tcpPort = 1020;
            this.udpPort = 1021;
            this.timeout = 5000;
        }

        public Builder withHost(String host) throws UnknownHostException {
            return withHost(InetAddress.getByName(host));
        }

        public Builder withHost(InetAddress host) throws UnknownHostException {
            this.host = host;
            return this;
        }

        public Builder withTcpPort(int tcpPort) {
            this.tcpPort = tcpPort;
            return this;
        }

        public Builder withUdpPort(int udpPort) {
            this.udpPort = udpPort;
            return this;
        }

        public Builder useUDP(boolean useUDP) {
            this.useUDP = useUDP;
            return this;
        }

        public Builder withTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Specifies whether or not, the executor (used for reporting) will be stopped with same time with reporter.
         * Default value is true.
         * Setting this parameter to false, has the sense in combining with providing external managed executor via {@link #scheduleOn(ScheduledExecutorService)}.
         *
         * @param shutdownExecutorOnStop if true, then executor will be stopped in same time with this reporter
         * @return {@code this}
         */
        public Builder shutdownExecutorOnStop(boolean shutdownExecutorOnStop) {
            this.shutdownExecutorOnStop = shutdownExecutorOnStop;
            return this;
        }

        /**
         * Specifies the executor to use while scheduling reporting of metrics.
         * Default value is null.
         * Null value leads to executor will be auto created on start.
         *
         * @param executor the executor to use while scheduling reporting of metrics.
         * @return {@code this}
         */
        public Builder scheduleOn(ScheduledExecutorService executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Convert rates to the given time unit.
         *
         * @param rateUnit a unit of time
         * @return {@code this}
         */
        public Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        /**
         * Convert durations to the given time unit.
         *
         * @param durationUnit a unit of time
         * @return {@code this}
         */
        public Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        /**
         * Only report metrics which match the given filter.
         *
         * @param filter a {@link MetricFilter}
         * @return {@code this}
         */
        public Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Builds a {@link ServerReporter} with the given properties.
         *
         * @return a {@link ServerReporter}
         */
        public ServerReporter build() throws IOException {
            return new ServerReporter(registry,
                    rateUnit,
                    durationUnit,
                    filter,
                    executor,
                    shutdownExecutorOnStop,
                    timeout,
                    host,
                    tcpPort,
                    udpPort,
                    useUDP);
        }
    }

    private ServerReporter(MetricRegistry registry,
                           TimeUnit rateUnit,
                           TimeUnit durationUnit,
                           MetricFilter filter,
                           ScheduledExecutorService executor,
                           boolean shutdownExecutorOnStop,
                           int timeout,
                           InetAddress host,
                           int tcpPort,
                           int udpPort,
                           boolean useUDP) throws IOException {
        super(registry, "console-reporter", filter, rateUnit, durationUnit, executor, shutdownExecutorOnStop, null);
        client = new KryoClient(timeout, host, tcpPort, udpPort, useUDP);
    }

    private KryoClient client;

    @Override
    public void report(SortedMap<String, Gauge> gauges,
                       SortedMap<String, Counter> counters,
                       SortedMap<String, Histogram> histograms,
                       SortedMap<String, Meter> meters,
                       SortedMap<String, Timer> timers) {
        if (!gauges.isEmpty()) {
            for (Map.Entry<String, Gauge> entry : gauges.entrySet()) {
                client.sendMetric(entry.getKey(), entry.getValue());
            }
        }

        if (!counters.isEmpty()) {
            for (Map.Entry<String, Counter> entry : counters.entrySet()) {
                client.sendMetric(entry.getKey(), entry.getValue());
            }
        }

        if (!histograms.isEmpty()) {
            for (Map.Entry<String, Histogram> entry : histograms.entrySet()) {
                client.sendMetric(entry.getKey(), entry.getValue());
            }
        }

        if (!meters.isEmpty()) {
            for (Map.Entry<String, Meter> entry : meters.entrySet()) {
                client.sendMetric(entry.getKey(), entry.getValue());
            }
        }

        if (!timers.isEmpty()) {
            for (Map.Entry<String, Timer> entry : timers.entrySet()) {
                client.sendMetric(entry.getKey(), entry.getValue());
            }
        }
    }

    private class KryoClient {
        private Client client;
        private boolean useUDP;

        private KryoClient(int timeout, InetAddress host, int tcpPort, int udpPort, boolean useUDP) throws IOException {
            this(new Client(), useUDP);
            getClient().start();
            KryoCommon.registerClasses(getClient().getKryo());
            getClient().connect(timeout, host, tcpPort, udpPort);
        }

        private KryoClient(Client client, boolean useUDP) {
            this.client = client;
            setUseUDP(useUDP);
        }

        public Client getClient() {
            return client;
        }

        public boolean isUseUDP() {
            return useUDP;
        }

        public void setUseUDP(boolean useUDP) {
            this.useUDP = useUDP;
        }

        public void sendMetric(String metricName, Metric metric) {
            if (isUseUDP()) {
                client.sendUDP(new MetricsUpdateRequest(metricName, metric));
            } else {
                client.sendTCP(new MetricsUpdateRequest(metricName, metric));
            }
        }
    }
}

