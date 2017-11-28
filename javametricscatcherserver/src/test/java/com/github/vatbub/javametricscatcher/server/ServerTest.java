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


import com.codahale.metrics.Gauge;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.github.vatbub.javametricscatcher.common.KryoCommon;
import org.junit.*;

import java.io.IOException;

public class ServerTest {
    private static Server server;
    private static final int tcpPort = 1020;
    private static final int udpPort = 1021;
    private Client kryoClient;

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
    }

    @After
    public void disconnectClient() throws InterruptedException {
        kryoClient.stop();
        Thread.sleep(2000);
    }

    @Test
    public void sendMeterTest() {
        kryoClient.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                Assert.fail();
            }
        });
        kryoClient.sendTCP((Gauge<Integer>) () -> 0);
    }
}
