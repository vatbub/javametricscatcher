package com.github.vatbub.javametricscatcher.server;

import com.github.vatbub.common.core.Common;
import com.github.vatbub.javametricscatcher.server.reporters.ConfigurationManager;
import org.jdom2.JDOMException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException, JDOMException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        Common.getInstance().setAppName("com.github.vatbub.javaMetricsCatcher.server");
        Server server = new Server(1020, 1021);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        ConfigurationManager.getInstance().readConfiguration(new File(Main.class.getResource("SampleConfig.xml").toURI()), server.getRegistry());
    }
}
