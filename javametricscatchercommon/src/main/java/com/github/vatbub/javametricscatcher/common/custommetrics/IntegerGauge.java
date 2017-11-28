package com.github.vatbub.javametricscatcher.common.custommetrics;

import com.codahale.metrics.Gauge;
import com.github.vatbub.javametricscatcher.common.SerializableMetric;

import java.util.HashMap;

public class IntegerGauge implements Gauge<Integer>, SerializableMetric<Integer> {
    public int value;

    @Override
    public Integer getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public Integer getSerializableData() {
        return getValue();
    }

    @Override
    public MetricType getMetricType() {
        return MetricType.INTEGER_GAUGE;
    }

    @Override
    public HashMap<String, String> getAdditionalMetadata() {
        return null;
    }
}
