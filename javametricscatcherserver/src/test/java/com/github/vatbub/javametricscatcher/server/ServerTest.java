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


import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.github.vatbub.common.core.logging.FOKLogger;
import com.github.vatbub.javametricscatcher.common.ExceptionMessage;
import com.github.vatbub.javametricscatcher.common.KryoCommon;
import com.github.vatbub.javametricscatcher.common.MetricsUpdateRequest;
import com.github.vatbub.javametricscatcher.common.MetricsUpdateResponse;
import com.github.vatbub.javametricscatcher.common.custommetrics.*;
import org.junit.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ServerTest {
    private static Server server;
    private static final int tcpPort = 1020;
    private static final int udpPort = 1021;
    private Client kryoClient;
    private boolean receivedResponse;
    private List<Throwable> thrownExceptions;

    @BeforeClass
    public static void startServer() throws IOException {
        System.out.println("Starting the server...");
        server = new Server(tcpPort, udpPort);
    }

    @AfterClass
    public static void stopServer() {
        System.out.println("Stopping the server...");
        server.stop();
    }

    @Before
    public void connectClient() throws IOException {
        System.out.println("Resetting the server...");
        server.reset();
        System.out.println("Connecting a new client...");
        kryoClient = new Client();
        kryoClient.start();
        KryoCommon.registerClasses(kryoClient.getKryo());
        kryoClient.connect(5000, "localhost", tcpPort, udpPort);
        receivedResponse = false;
        thrownExceptions = new LinkedList<>();
    }

    @After
    public void disconnectClient() throws Throwable {
        System.out.println("Disconnecting the client...");
        while (!receivedResponse) {
            Thread.sleep(50);
        }
        System.out.println("Donw waiting...");
        kryoClient.stop();

        System.out.println("Rethrowing exceptions...");
        if (thrownExceptions.size() > 0) {
            throw thrownExceptions.get(0);
        }
        Thread.sleep(2000);
    }

    @Test
    public void sendIntegerGaugeTest() {
        int gaugeValue = 100;
        String metricName = "testMetric.integerGauge";
        addGaugeAndHistogramResponseListener(metricName);

        IntegerGauge gauge = new IntegerGauge();
        gauge.setValue(gaugeValue);
        kryoClient.sendTCP(new MetricsUpdateRequest(metricName, gauge.getMetricType(), gauge.getSerializableData(), gauge.getAdditionalMetadata()));
    }

    @Test
    public void sendLongGaugeTest() {
        long gaugeValue = 100;
        String metricName = "testMetric.longGauge";
        addGaugeAndHistogramResponseListener(metricName);

        LongGauge gauge = new LongGauge();
        gauge.setValue(gaugeValue);
        kryoClient.sendTCP(new MetricsUpdateRequest(metricName, gauge.getMetricType(), gauge.getSerializableData(), gauge.getAdditionalMetadata()));
    }

    @Test
    public void sendTimerTest() {
        String metricName = "testMetric.timer";
        kryoClient.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                executeReceivedHandler(() -> {
                    Assert.assertTrue(object instanceof MetricsUpdateResponse);
                    Assert.assertEquals(1, server.getRegistry().getTimers().size());
                    Assert.assertTrue(server.getRegistry().getTimers().containsKey(metricName));
                    Assert.assertEquals(1, server.getRegistry().getTimers().get(metricName).getCount());
                });
            }
        });

        CustomTimer timer = new CustomTimer();
        timer.update(100, TimeUnit.SECONDS);
        kryoClient.sendTCP(new MetricsUpdateRequest(metricName, timer.getMetricType(), timer.getSerializableData(), timer.getAdditionalMetadata()));
    }

    @Test
    public void sendHistogramTest() {
        String metricName = "testMetric.histogram";
        addGaugeAndHistogramResponseListener(metricName);

        CustomHistogram histogram = new CustomHistogram();
        histogram.update(100);
        kryoClient.sendTCP(new MetricsUpdateRequest(metricName, histogram.getMetricType(), histogram.getSerializableData(), histogram.getAdditionalMetadata()));
    }

    @Test
    public void sendCounterTest() throws InterruptedException {
        long targetValue = 10;
        CustomCounter counter = new CustomCounter();
        final boolean[] waitForNextResponse = {false};
        String metricName = "testMetric.counter";
        kryoClient.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                executeReceivedHandler(() -> {
                    try {
                        if (object instanceof FrameworkMessage.KeepAlive)
                            return;

                        FOKLogger.info(ServerTest.class.getName(), "Received a response of type " + object.getClass().getName() + ", performing assertions...");
                        Assert.assertTrue(object instanceof MetricsUpdateResponse);
                        Assert.assertEquals(1, server.getRegistry().getCounters().size());
                        Assert.assertTrue(server.getRegistry().getCounters().containsKey(metricName));
                        Assert.assertEquals(counter.getCount(), server.getRegistry().getCounters().get(metricName).getCount());
                        waitForNextResponse[0] = false;
                    } catch (Throwable t) {
                        waitForNextResponse[0] = false;
                        throw t;
                    }
                }, counter.getCount() == targetValue);
            }
        });

        for (int i = 0; i <= targetValue; i++) {
            FOKLogger.info(ServerTest.class.getName(), "Waiting for the previous request to pass...");
            while (waitForNextResponse[0]) {
                Thread.sleep(50);
            }
            waitForNextResponse[0] = true;
            counter.inc();
            FOKLogger.info(ServerTest.class.getName(), "Sending counter value " + counter.getCount());
            kryoClient.sendTCP(new MetricsUpdateRequest(metricName, counter.getMetricType(), counter.getSerializableData(), counter.getAdditionalMetadata()));
        }
    }

    @Test
    public void sendIllegalObjectTest() {
        kryoClient.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                executeReceivedHandler(() -> {
                    Assert.assertTrue(object instanceof ExceptionMessage);
                    System.out.println("Received exception from server:\n" + object.toString());
                });
            }
        });
        kryoClient.sendTCP("Hello");
    }

    private void addGaugeAndHistogramResponseListener(String metricName) {
        kryoClient.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                executeReceivedHandler(() -> {
                    Assert.assertTrue(object instanceof MetricsUpdateResponse);
                    Assert.assertEquals(1, server.getRegistry().getHistograms().size());
                    Assert.assertTrue(server.getRegistry().getHistograms().containsKey(metricName));
                    Assert.assertEquals(1, server.getRegistry().getHistograms().get(metricName).getCount());
                });
            }
        });
    }

    public void executeReceivedHandler(Runnable handler) {
        executeReceivedHandler(handler, true);
    }

    public void executeReceivedHandler(Runnable handler, boolean setReceivedResponse) {
        try {
            handler.run();
            receivedResponse = setReceivedResponse;
        } catch (Throwable e) {
            e.printStackTrace();
            thrownExceptions.add(e);
            receivedResponse = true;
        }
    }
}
