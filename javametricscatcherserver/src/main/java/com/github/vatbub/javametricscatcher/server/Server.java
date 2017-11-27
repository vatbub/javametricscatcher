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
import com.github.vatbub.javametricscatcher.common.IllegalRequestException;
import com.github.vatbub.javametricscatcher.common.MetricsUpdateRequest;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

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
                    } else if (request.getMetric() instanceof Histogram) {
                        // TODO
                    } else if (request.getMetric() instanceof Meter) {
                        // TODO
                    } else if (request.getMetric() instanceof Timer) {
                        // TODO
                    }
                } else {
                    throw new IllegalRequestException("Illegal object received :" + object.getClass().getName());
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
