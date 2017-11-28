package com.github.vatbub.javametricscatcher.common.custommetrics;

import com.codahale.metrics.Gauge;
import com.github.vatbub.javametricscatcher.common.SerializableMetric;

import java.util.HashMap;

public class LongGauge implements Gauge<Long>, SerializableMetric<Long> {
    public long value;

    @Override
    public Long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public Long getSerializableData() {
        return getValue();
    }

    @Override
    public MetricType getMetricType() {
        return MetricType.LONG_GAUGE;
    }

    @Override
    public HashMap<String, String> getAdditionalMetadata() {
        return null;
    }
}
