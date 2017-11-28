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
import com.github.vatbub.javametricscatcher.common.custommetrics.CustomCounter;
import com.github.vatbub.javametricscatcher.common.custommetrics.IntegerGauge;
import com.github.vatbub.javametricscatcher.common.custommetrics.LongGauge;
import org.junit.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ServerTest {
    private static Server server;
    private static final int tcpPort = 1020;
    private static final int udpPort = 1021;
    private Client kryoClient;
    private boolean receivedResponse;
    private List<Throwable> thrownExceptions;

    @BeforeClass
    public static void startServer() throws IOException {
        server = new Server(tcpPort, udpPort);
    }

    @AfterClass
    public static void stopServer() {
        server.stop();
    }

    @Before
    public void connectClient() throws IOException {
        server.reset();
        kryoClient = new Client();
        kryoClient.start();
        KryoCommon.registerClasses(kryoClient.getKryo());
        kryoClient.connect(5000, "localhost", tcpPort, udpPort);
        receivedResponse = false;
        thrownExceptions = new LinkedList<>();
    }

    @After
    public void disconnectClient() throws Throwable {
        while (!receivedResponse) {
            Thread.sleep(50);
        }

        kryoClient.stop();
        if (thrownExceptions.size() > 0) {
            throw thrownExceptions.get(0);
        }
        Thread.sleep(2000);
    }

    @Test
    public void sendIntegerGaugeTest() throws InterruptedException {
        int gaugeValue = 100;
        String metricName = "testMetric.integerGauge";
        addGaugeResponseListener(metricName);

        IntegerGauge gauge = new IntegerGauge();
        gauge.setValue(gaugeValue);
        kryoClient.sendTCP(new MetricsUpdateRequest(metricName, gauge.getMetricType(), gauge.getSerializableData(), gauge.getAdditionalMetadata()));
    }

    @Test
    public void sendLongGaugeTest() throws InterruptedException {
        long gaugeValue = 100;
        String metricName = "testMetric.longGauge";
        addGaugeResponseListener(metricName);

        LongGauge gauge = new LongGauge();
        gauge.setValue(gaugeValue);
        kryoClient.sendTCP(new MetricsUpdateRequest(metricName, gauge.getMetricType(), gauge.getSerializableData(), gauge.getAdditionalMetadata()));
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

    private void addGaugeResponseListener(String metricName) {
        kryoClient.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                executeReceivedHandler(() -> {
                    Assert.assertTrue(object instanceof MetricsUpdateResponse);
                    Assert.assertEquals(1, server.getRegistry().getHistograms().size());
                    Assert.assertTrue(server.getRegistry().getHistograms().containsKey(metricName));
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