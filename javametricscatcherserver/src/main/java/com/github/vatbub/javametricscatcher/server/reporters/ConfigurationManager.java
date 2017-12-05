package com.github.vatbub.javametricscatcher.server.reporters;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.ServiceLoader;

public class ConfigurationManager {
    private static ConfigurationManager ourInstance = new ConfigurationManager();

    public static ConfigurationManager getInstance() {
        return ourInstance;
    }

    private ConfigurationManager() {
    }

    public void readConfiguration(File configFile) throws FileNotFoundException, YamlException {
        YamlReader reader = new YamlReader(new FileReader(configFile));
        Map document = (Map) reader.read();

        ServiceLoader<ReporterConfigurator> loader = ServiceLoader.load(ReporterConfigurator.class);
        for (ReporterConfigurator implClass : loader) {

        }
    }
}
