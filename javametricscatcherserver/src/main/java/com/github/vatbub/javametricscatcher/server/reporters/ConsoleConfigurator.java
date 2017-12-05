package com.github.vatbub.javametricscatcher.server.reporters;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConsoleConfigurator implements ReporterConfigurator<ConsoleReporter> {
    @Override
    public ConsoleReporter configure(MetricRegistry registry, Map<String, Object> yamlConfig) {
        return ConsoleReporter.forRegistry(registry).build();
    }

    @Override
    public List<String> getAliases() {
        List<String> res = new ArrayList<>(3);
        res.add("ConsoleReporter");
        res.add("consoleReporter");
        res.add("consolereporter");
        return res;
    }
}
