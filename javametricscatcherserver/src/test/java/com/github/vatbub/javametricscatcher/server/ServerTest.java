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
import com.esotericsoftware.kryonet.Listener;
import com.github.vatbub.javametricscatcher.common.KryoCommon;
import com.github.vatbub.javametricscatcher.common.MetricsUpdateRequest;
import com.github.vatbub.javametricscatcher.common.MetricsUpdateResponse;
import com.github.vatbub.javametricscatcher.common.custommetrics.IntegerGauge;
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
        System.out.println("Number of thrown exceptions: " + thrownExceptions.size());
        if (thrownExceptions.size() > 0) {
            throw thrownExceptions.get(0);
        }
        Thread.sleep(2000);
    }

    @Test
    public void sendIntegerGaugeTest() throws InterruptedException {
        kryoClient.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                executeReceivedHandler(() -> Assert.assertTrue(object instanceof MetricsUpdateResponse));
            }
        });
        IntegerGauge gauge = new IntegerGauge();
        gauge.setValue(100);
        kryoClient.sendTCP(new MetricsUpdateRequest("testMetric", gauge.getMetricType(), gauge.getSerializableData(), gauge.getAdditionalMetadata()));
    }

    public void executeReceivedHandler(Runnable handler){
        try {
            handler.run();
        } catch (Throwable e) {
            thrownExceptions.add(e);
        } finally {
            receivedResponse = true;
        }
    }
}
