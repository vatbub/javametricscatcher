package com.github.vatbub.javametricscatcher.common.custommetrics;

import com.codahale.metrics.Counter;
import com.github.vatbub.javametricscatcher.common.SerializableMetric;

import java.util.HashMap;

public class CustomCounter extends Counter implements SerializableMetric<Long>{
    @Override
    public Long getSerializableData() {
        return getCount();
    }

    @Override
    public MetricType getMetricType() {
        return MetricType.COUNTER;
    }

    @Override
    public HashMap<String, String> getAdditionalMetadata() {
        return null;
    }
}
