package com.github.vatbub.javametricscatcher.sampleapp.metricviews;

/*-
 * #%L
 * javametricscatcher.common
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
import com.github.vatbub.javametricscatcher.common.SerializableMetric;
import com.github.vatbub.javametricscatcher.common.custommetrics.MetricType;

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
