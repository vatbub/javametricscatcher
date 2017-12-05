package com.github.vatbub.javametricscatcher.server.reporters;

import com.codahale.metrics.MetricRegistry;

import java.util.List;
import java.util.Map;

public interface ReporterConfigurator<T> {
    T configure(MetricRegistry registry, Map<String, Object> yamlConfig);

    List<String> getAliases();
}
