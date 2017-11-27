package com.github.vatbub.javametricscatcher.server;

/*-
 * #%L
 * javametricscatcher.server
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
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.github.vatbub.common.core.logging.FOKLogger;
import com.github.vatbub.javametricscatcher.common.*;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;

public class Server {
    private com.esotericsoftware.kryonet.Server server;
    private MetricRegistry registry;
    private PreviousValueMap previousValueMap;

    public Server(int tcpPort, int udpPort) throws IOException {
        registry = new MetricRegistry();
        previousValueMap = new PreviousValueMap();
        server = new com.esotericsoftware.kryonet.Server();
        server.start();
        server.bind(tcpPort, udpPort);

        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                previousValueMap.put(connection, new PreviousValueMap.ValueCollection());
            }

            @Override
            public void disconnected(Connection connection) {
                PreviousValueMap.ValueCollection valueCollection = previousValueMap.remove(connection);

                // remove counts from that connection
                if (!valueCollection.getCounterMap().isEmpty()) {
                    for (Map.Entry<String, Counter> entry : valueCollection.getCounterMap().entrySet()) {
                        getRegistry().counter(entry.getKey()).dec(entry.getValue().getCount());
                    }
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public void received(Connection connection, Object object) {
                try {
                    if (object instanceof FrameworkMessage.KeepAlive) {
                        FOKLogger.info(Server.class.getName(), "Received a keepAlive message from a client");
                    } else if (object instanceof MetricsUpdateRequest) {
                        MetricsUpdateRequest request = (MetricsUpdateRequest) object;
                        if (request.getMetric() instanceof Gauge) {
                            Gauge remoteGauge = (Gauge) request.getMetric();
                            Histogram localHistogramForGauge = getRegistry().histogram(request.getMetricName());
                            if (getType(remoteGauge) == Integer.class) {
                                localHistogramForGauge.update(((Gauge<Integer>) remoteGauge).getValue());
                            } else if (getType(remoteGauge) == Long.class) {
                                localHistogramForGauge.update(((Gauge<Long>) remoteGauge).getValue());
                            } else {
                                throw new IllegalRequestException("Only gauges of type Integer and Long are supported.");
                            }
                        } else if (request.getMetric() instanceof Counter) {
                            if (!previousValueMap.get(connection).getCounterMap().containsKey(request.getMetricName())) {
                                previousValueMap.get(connection).getCounterMap().put(request.getMetricName(), new Counter());
                            }

                            Counter remoteCounter = (Counter) request.getMetric();
                            Counter localCounter = getRegistry().counter(request.getMetricName());
                            Counter previousRemoteCounter = previousValueMap.get(connection).getCounterMap().get(request.getMetricName());

                            long diff = previousRemoteCounter.getCount() - remoteCounter.getCount();
                            localCounter.inc(diff);
                            // set the previous value to the sent value
                            previousRemoteCounter.inc(diff);
                            assert previousRemoteCounter.getCount() == remoteCounter.getCount();
                        } else if (request.getMetric() instanceof CustomHistogram) {
                            CustomHistogram remoteHistogram = (CustomHistogram) request.getMetric();

                            if (!previousValueMap.get(connection).getHistogramMap().containsKey(request.getMetricName())) {
                                try {
                                    previousValueMap.get(connection).getHistogramMap().put(request.getMetricName(), new CustomHistogram(remoteHistogram.getNewInstanceOfSameReservoirType()));
                                } catch (IllegalAccessException | InstantiationException e) {
                                    FOKLogger.log(Server.class.getName(), Level.SEVERE, "Unable to create a new CustomHistogram for the previousValueMap with a reservoir of type " + remoteHistogram.getReservoir().getClass().getName() + ", creating a new CustomHistogram with the default reservoir", e);
                                    previousValueMap.get(connection).getHistogramMap().put(request.getMetricName(), new CustomHistogram());
                                }
                            }

                            Histogram localHistogram = getRegistry().histogram(request.getMetricName());
                            CustomHistogram previousRemoteHistogram = previousValueMap.get(connection).getHistogramMap().get(request.getMetricName());
                            Collection<Long> diff = CollectionUtils.disjunction(remoteHistogram.getUpdateHistory(), previousRemoteHistogram.getUpdateHistory());

                            for (long value : diff) {
                                localHistogram.update(value);
                                previousRemoteHistogram.update(value);
                            }
                            assert remoteHistogram.getUpdateHistory().containsAll(previousRemoteHistogram.getUpdateHistory()) && previousRemoteHistogram.getUpdateHistory().containsAll(remoteHistogram.getUpdateHistory());
                        } else if (request.getMetric() instanceof CustomTimer) {
                            CustomTimer remoteTimer = (CustomTimer) request.getMetric();

                            if (!previousValueMap.get(connection).getTimerMap().containsKey(request.getMetricName())) {
                                try {
                                    previousValueMap.get(connection).getTimerMap().put(request.getMetricName(), new CustomTimer(remoteTimer.getNewInstanceOfSameReservoirType(), remoteTimer.getClock()));
                                } catch (IllegalAccessException | InstantiationException e) {
                                    FOKLogger.log(Server.class.getName(), Level.SEVERE, "Unable to create a new CustomTimer for the previousValueMap with a reservoir of type " + remoteTimer.getReservoir().getClass().getName() + ", creating a new CustomHistogram with the default reservoir and clock", e);
                                    previousValueMap.get(connection).getTimerMap().put(request.getMetricName(), new CustomTimer());
                                }
                            }

                            Timer localTimer = getRegistry().timer(request.getMetricName());
                            CustomTimer previousRemoteTimer = previousValueMap.get(connection).getTimerMap().get(request.getMetricName());
                            Collection<CustomTimer.TimerEntry> diff = CollectionUtils.disjunction(remoteTimer.getUpdateHistory(), previousRemoteTimer.getUpdateHistory());

                            for (CustomTimer.TimerEntry entry : diff) {
                                localTimer.update(entry.getDuration(), entry.getTimeUnit());
                                previousRemoteTimer.update(entry.getDuration(), entry.getTimeUnit());
                            }
                            assert remoteTimer.getUpdateHistory().containsAll(previousRemoteTimer.getUpdateHistory()) && previousRemoteTimer.getUpdateHistory().containsAll(remoteTimer.getUpdateHistory());
                        } else if (request.getMetric() instanceof Meter) {
                            throw new IllegalRequestException("Metrics of type Meter are not supported.");
                        } else if (request.getMetric() instanceof Histogram || request.getMetric() instanceof Timer) {
                            throw new IllegalRequestException("Metrics of type Histogram and Timer are not supported, use CustomHistogram and CustomTimer instead.");
                        }
                    } else {
                        throw new IllegalRequestException("Illegal object received :" + object.getClass().getName());
                    }
                } catch (Exception e) {
                    FOKLogger.log(Server.class.getName(), Level.SEVERE, FOKLogger.DEFAULT_ERROR_TEXT, e);
                    connection.sendTCP(ExceptionMessage.fromThrowable(e));
                }
            }
        });
    }

    private Class getType(Object object) {
        ParameterizedType parameterizedType = (ParameterizedType) object.getClass()
                .getGenericSuperclass();
        return (Class) parameterizedType.getActualTypeArguments()[0];
    }

    public MetricRegistry getRegistry() {
        return registry;
    }
}
