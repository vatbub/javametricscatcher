package com.github.vatbub.javametricscatcher.common;

import com.codahale.metrics.Metric;

public class MetricsUpdateRequest {
    private String metricName;
    private Metric metric;

    public MetricsUpdateRequest(){
        this(null, null);
    }

    public MetricsUpdateRequest(String metricName, Metric metric){
        setMetricName(metricName);
        setMetric(metric);
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }
}
